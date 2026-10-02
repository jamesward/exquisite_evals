package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.memory.ChatMemory
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.json.JsonMapper
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Everything captured from one agent execution of one task on one arm. */
data class RunRecord(
    val task: String,
    val arm: String,
    val answer: String?,
    val error: String?,
    val durationMs: Long,
    val modelCalls: Int,
    val inputTokens: Int,
    val outputTokens: Int,
    /** Tokens spent by models called *inside* tools (e.g. WebFetch summaries). */
    val overheadInputTokens: Int,
    val overheadOutputTokens: Int,
    val toolCalls: List<RecordedToolCall>,
    val hostedToolCalls: Int,
    val toolsOfferedPerTurn: List<Int>,
    /** Set when the budget forced a tool-free final answer. */
    val wrapUp: String? = null,
) {
    val totalTokens get() = inputTokens + outputTokens + overheadInputTokens + overheadOutputTokens
}

/** Which models produced a run; written to `meta.json` and the report header, since results are compared across runs. */
data class RunModels(val agent: String = "unknown", val agentApi: String = "unknown", val llmJudge: String = "unknown", val jevJudge: String = "TypeSafe Jev (client default)")

/** One cell of the results matrix: the run, the deterministic checks, and every judge's verdict. */
data class EvalResult(val run: RunRecord, val checks: CodeCheckResult, val judges: List<JudgeResult>) {
    fun judge(name: String) = judges.firstOrNull { it.judge == name }
}

/**
 * Runs every selected task on every selected arm (single trial), then judges each answer with *all* judges.
 * Arms share the system prompt below; only their tools/advisors differ.
 */
class EvalRunner(
    private val catalog: ArmCatalog,
    private val judges: List<EvalJudge>,
    private val budget: RunBudget = RunBudget(),
    /** Outside `build/` on purpose: `gradlew clean` must not delete past results. */
    private val outputRoot: Path = Path.of("results"),
    /** Appended to the run directory name, e.g. `post-mcp-changes`. */
    private val label: String = "",
    private val models: RunModels = RunModels(),
) {
    private val log = LoggerFactory.getLogger(EvalRunner::class.java)

    val systemPrompt = """
        You are an AI coding assistant helping a Java developer. Answer questions about Java libraries accurately.
        Library versions and APIs change after your training data, so use the tools available to you to look things up
        instead of relying on memory: resolve the exact Maven coordinates and the latest version before describing an API.
        If something the user asks about does not exist, say so plainly. If you could not verify a fact, say so rather than guess.
        Today's date is ${LocalDate.now()}. Keep the final answer concise and specific.
    """.trimIndent()

    fun run(tasks: List<EvalTask>, arms: List<Arm>): List<EvalResult> {
        log.info("Running {} task(s) x {} arm(s): tasks={} arms={} models={}", tasks.size, arms.size, tasks.map { it.id }, arms.map { it.id }, models)
        // Warn, don't fail: a stale reference makes a correct agent look wrong, which is worth knowing up front.
        runCatching { ReferenceFreshness.check(tasks) }
            .onSuccess { stale -> stale.forEach { log.warn("STALE REFERENCE: {}", it) } }
            .onFailure { log.warn("Could not check reference freshness against Maven Central: {}", it.message) }
        val results = tasks.flatMap { task ->
            arms.map { arm ->
                val record = execute(task, arm)
                val checks = CodeChecks.evaluate(task, record.answer)
                val verdicts = judges.map { it.judge(task, record, checks) }
                log.info("[{}/{}] {} | {}", task.id, arm.id, checks,
                    verdicts.joinToString { "${it.judge}=${if (it.passed) "PASS" else "FAIL"}(%.2f)".format(it.score) })
                EvalResult(record, checks, verdicts)
            }
        }
        Report.print(results, catalog.disabled(), models)
        val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        Report.write(results, outputRoot.resolve(if (label.isBlank()) stamp else "$stamp-$label"), models)
        return results
    }

    fun execute(task: EvalTask, arm: Arm): RunRecord {
        val sessionId = "${task.id}-${arm.id}-${System.nanoTime()}"
        val workDir = Files.createTempDirectory("eval-${arm.id}-")
        val run = ArmRun(sessionId, workDir)  // closed below: removes the run's sandbox container, if any
        val tracker = TokenTracker("${task.id}/${arm.id}")
        val start = System.nanoTime()
        val (answer, error) = try {
            val tools = RunToolCalling.capped(arm.tools(run), budget.maxToolCalls)
            val advisors = buildList {
                if (tools.isNotEmpty()) add(arm.toolAdvisor(run, RunToolCalling.manager(budget, { tools }, run.toolCalls)))
                add(TokenTrackingAdvisor(tracker, budget))
            }
            var spec = arm.client().build().prompt().system(systemPrompt).user(task.prompt)
                .advisors(advisors)
                // the tool-search advisor scopes its index per session; harmless for the other arms
                .advisors { it.param(ChatMemory.CONVERSATION_ID, sessionId) }
            if (tools.isNotEmpty()) spec = spec.tools(*tools.toTypedArray())
            spec.call().content() to null
        } catch (e: Exception) {
            val msg = generateSequence<Throwable>(e) { it.cause }.joinToString(" <- ") { "${it.javaClass.simpleName}: ${it.message}" }
            log.warn("[{}/{}] run failed: {}", task.id, arm.id, msg)
            null to msg
        } finally {
            run.close()
            workDir.toFile().deleteRecursively()
        }
        return RunRecord(
            task.id, arm.id, answer, error, (System.nanoTime() - start) / 1_000_000,
            tracker.modelCalls, tracker.promptTokens, tracker.completionTokens,
            run.overhead.promptTokens, run.overhead.completionTokens,
            run.toolCalls.toList(), tracker.hostedToolCalls.size, tracker.toolsOfferedPerTurn.map { it.size }, tracker.wrapUpReason,
        )
    }
}

object Report {

    private val mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build()

    private fun verdict(r: JudgeResult?) = when {
        r == null -> "-"
        r.error != null -> "ERR"
        else -> "${if (r.passed) "PASS" else "FAIL"} %.2f".format(r.score)
    }

    fun table(results: List<EvalResult>): String = buildString {
        val header = "| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |"
        appendLine(header)
        appendLine("|" + "---|".repeat(header.count { it == '|' } - 1))
        results.forEach { r ->
            val run = r.run
            val checks = "${r.checks.found.size}/${r.checks.found.size + r.checks.missing.size}" +
                (if (r.checks.hallucinatedNames.isNotEmpty()) " +${r.checks.hallucinatedNames.size} invented" else "") +
                (if (run.error != null) " (run error)" else "") + (if (run.wrapUp != null) " (budget)" else "")
            appendLine("| ${run.task} | ${run.arm} | $checks | ${verdict(r.judge("llm"))} | ${verdict(r.judge("jev"))} | " +
                "${run.inputTokens} | ${run.outputTokens} | ${run.overheadInputTokens + run.overheadOutputTokens} | " +
                "${run.modelCalls} | ${run.toolCalls.size + run.hostedToolCalls} | ${"%.1f".format(run.durationMs / 1000.0)} |")
        }
    }

    /** Per-arm totals across tasks: the headline comparison. */
    fun armSummary(results: List<EvalResult>): String = buildString {
        appendLine("| arm | checks pass | llm pass | jev pass | total tokens | avg time s |")
        appendLine("|---|---|---|---|---|---|")
        results.groupBy { it.run.arm }.forEach { (arm, rs) ->
            fun rate(p: (EvalResult) -> Boolean) = "${rs.count(p)}/${rs.size}"
            appendLine("| $arm | ${rate { it.checks.passed }} | ${rate { it.judge("llm")?.passed == true }} | " +
                "${rate { it.judge("jev")?.passed == true }} | ${rs.sumOf { it.run.totalTokens }} | " +
                "${"%.1f".format(rs.map { it.run.durationMs }.average() / 1000.0)} |")
        }
    }

    /** What each judge cost, and how often the judges (and the code checks) agree. */
    fun judgeSummary(results: List<EvalResult>): String = buildString {
        appendLine("| judge | pass rate | in tok | out tok | total time s | errors |")
        appendLine("|---|---|---|---|---|---|")
        results.flatMap { it.judges }.groupBy { it.judge }.forEach { (name, js) ->
            appendLine("| $name | ${js.count { it.passed }}/${js.size} | ${js.sumOf { it.inputTokens }} | ${js.sumOf { it.outputTokens }} | " +
                "${"%.1f".format(js.sumOf { it.durationMs } / 1000.0)} | ${js.count { it.error != null }} |")
        }
        fun agree(a: (EvalResult) -> Boolean?, b: (EvalResult) -> Boolean?): String {
            val pairs = results.mapNotNull { r -> a(r)?.let { x -> b(r)?.let { y -> x to y } } }
            return if (pairs.isEmpty()) "-" else "${pairs.count { it.first == it.second }}/${pairs.size}"
        }
        val llm = { r: EvalResult -> r.judge("llm")?.takeIf { it.error == null }?.passed }
        val jev = { r: EvalResult -> r.judge("jev")?.takeIf { it.error == null }?.passed }
        val llmSem = { r: EvalResult -> r.judge("llm")?.takeIf { it.error == null }?.criteria?.get("semantic_pass") as Boolean? }
        val jevSem = { r: EvalResult -> r.judge("jev")?.takeIf { it.error == null }?.criteria?.get("semantic_pass") as Boolean? }
        appendLine()
        appendLine("Agreement: llm vs jev ${agree(llm, jev)} | semantic-only llm vs jev ${agree(llmSem, jevSem)} | " +
            "llm-semantic vs code checks ${agree(llmSem) { it.checks.passed }} | jev-semantic vs code checks ${agree(jevSem) { it.checks.passed }}")
    }

    fun modelsLine(models: RunModels) =
        "Agent: ${models.agent} (${models.agentApi}) · LLM judge: ${models.llmJudge} · Jev judge: ${models.jevJudge}"

    fun print(results: List<EvalResult>, disabledArms: Map<String, String>, models: RunModels = RunModels()) {
        println("\n" + "=".repeat(100))
        println("EVAL RESULTS — " + modelsLine(models))
        println("=".repeat(100))
        results.forEach { r ->
            println("\n----- ${r.run.task} / ${r.run.arm} -----")
            r.run.wrapUp?.let { println("[budget: $it — final answer forced without tools]") }
            println(r.run.answer?.trim() ?: "(no answer) ${r.run.error}")
            println("checks: ${r.checks}")
            r.judges.forEach { j -> println("${j.judge}: ${verdict(j)} ${j.criteria} ${j.error ?: j.rationale.replace('\n', ' ').take(300)}") }
        }
        println()
        print(table(results))
        println()
        print(armSummary(results))
        println()
        print(judgeSummary(results))
        if (disabledArms.isNotEmpty()) println("\nDisabled arms: " + disabledArms.entries.joinToString { "${it.key} (${it.value})" })
        println("=".repeat(100))
    }

    /** One row per task x arm, for comparing runs (see `compare.py`). */
    fun summaryCsv(results: List<EvalResult>): String = buildString {
        appendLine("task,arm,facts_found,facts_total,invented,budget,run_error,checks_pass,llm_pass,llm_score,jev_pass,jev_score," +
            "in_tokens,out_tokens,in_tool_tokens,model_calls,tool_calls,time_s")
        results.forEach { r ->
            val run = r.run
            fun j(name: String) = r.judge(name)
            appendLine(listOf(
                run.task, run.arm, r.checks.found.size, r.checks.found.size + r.checks.missing.size, r.checks.hallucinatedNames.size,
                run.wrapUp != null, run.error != null, r.checks.passed,
                j("llm")?.passed, j("llm")?.score?.let { "%.2f".format(it) }, j("jev")?.passed, j("jev")?.score?.let { "%.2f".format(it) },
                run.inputTokens, run.outputTokens, run.overheadInputTokens + run.overheadOutputTokens,
                run.modelCalls, run.toolCalls.size + run.hostedToolCalls, "%.1f".format(run.durationMs / 1000.0),
            ).joinToString(","))
        }
    }

    fun write(results: List<EvalResult>, dir: Path, models: RunModels = RunModels()) {
        Files.createDirectories(dir)
        mapper.writeValue(dir.resolve("meta.json").toFile(), models)
        mapper.writeValue(dir.resolve("results.json").toFile(), results)
        Files.writeString(dir.resolve("summary.csv"), summaryCsv(results))
        Files.writeString(dir.resolve("report.md"),
            "# Eval results\n\n" + modelsLine(models) + "\n\n" + table(results) + "\n## By arm\n\n" + armSummary(results) + "\n## Judges\n\n" + judgeSummary(results) +
                results.joinToString("") { r ->
                    "\n## ${r.run.task} / ${r.run.arm}\n\n```\n${r.run.answer?.trim() ?: r.run.error}\n```\n\n- checks: ${r.checks}\n" +
                        r.judges.joinToString("") { j -> "- ${j.judge}: ${verdict(j)} ${j.criteria} ${j.error ?: j.rationale}\n" }
                })
        println("Wrote ${dir.resolve("report.md")}, results.json and summary.csv")
    }
}
