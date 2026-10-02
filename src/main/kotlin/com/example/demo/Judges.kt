package com.example.demo

import org.slf4j.LoggerFactory
import org.springaicommunity.typesafe.TypeSafeClient
import org.springaicommunity.typesafe.judge.JevCriterion
import org.springaicommunity.typesafe.judge.JevFinding
import org.springaicommunity.typesafe.judge.JevJudge
import org.springaicommunity.typesafe.judge.JevJudgeInput
import org.springaicommunity.typesafe.question.Noul
import org.springaicommunity.typesafe.question.Score
import org.springframework.ai.chat.client.ChatClient
import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper

/** One judge's verdict on one run. [score] is 0..1; [criteria] are the judge's own per-dimension results. */
data class JudgeResult(
    val judge: String,
    val passed: Boolean,
    val score: Double,
    val criteria: Map<String, Any?>,
    val rationale: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val durationMs: Long,
    val error: String? = null,
) {
    companion object {
        fun failed(judge: String, e: Throwable, ms: Long) =
            JudgeResult(judge, false, 0.0, emptyMap(), "", 0, 0, ms, e.message ?: e.javaClass.simpleName)
    }
}

/**
 * A judge grades an answer against the task's frozen reference. Both judges see the same inputs, and both
 * fold in the deterministic [CodeCheckResult] (a missing version or an invented class name always fails),
 * so their pass/fail is comparable; their semantic criteria differ in how they are asked.
 */
interface EvalJudge {
    val name: String
    fun judge(task: EvalTask, run: RunRecord, checks: CodeCheckResult): JudgeResult
}

private inline fun <T> timed(block: () -> T): Pair<T, Long> {
    val start = System.nanoTime()
    val value = block()
    return value to (System.nanoTime() - start) / 1_000_000
}

/** LLM as judge: one chat call asking for a JSON verdict (parsed leniently, so a chatty model still works). */
class LlmJudge(private val judgeClient: ChatClient.Builder) : EvalJudge {

    override val name = "llm"
    private val mapper = JsonMapper.builder().build()
    private val log = LoggerFactory.getLogger(LlmJudge::class.java)

    fun prompt(task: EvalTask, answer: String) = """
        You are grading an AI assistant's answer to a question about a Java library, against a verified reference answer.

        QUESTION:
        ${task.prompt}

        REFERENCE ANSWER (verified from the published artifacts; treat it as ground truth):
        ${task.reference}

        ASSISTANT ANSWER:
        $answer

        Grade the assistant answer:
        - factually_consistent: true only if every version, coordinate, class, method, default value and behaviour the
          answer states agrees with the reference. A claim that contradicts the reference, or names an API the reference
          does not contain, makes this false. Omissions do not make it false.
        - completeness: integer 0-4, how much of what the question asks the answer covers correctly (4 = all of it).
        - hallucinations: array of the specific unsupported or wrong claims (e.g. invented method names, wrong versions); [] if none.
        - trap_handled: ${if (task.trap) "true if the answer clearly says the non-existent API does not exist, else false" else "null (not applicable)"}.
        - rationale: one or two sentences.
        Reply with ONLY a JSON object with exactly the keys factually_consistent, completeness, hallucinations, trap_handled, rationale.
    """.trimIndent()

    override fun judge(task: EvalTask, run: RunRecord, checks: CodeCheckResult): JudgeResult {
        val tracker = TokenTracker("judge:llm")
        val start = System.nanoTime()
        return try {
            val (text, ms) = timed {
                judgeClient.clone().build().prompt().user(prompt(task, run.answer.orEmpty()))
                    .advisors(TokenTrackingAdvisor(tracker)).call().content().orEmpty()
            }
            val v = parseJson(text)
            val consistent = v.path("factually_consistent").asBoolean(false)
            val completeness = v.path("completeness").asInt(0).coerceIn(0, 4)
            val hallucinations = v.path("hallucinations").let { n -> if (n.isArray) (0 until n.size()).map { n.get(it).asString() } else emptyList() }
            val trap = v.path("trap_handled").takeIf { it.isBoolean }?.asBoolean()
            val semantic = consistent && completeness >= 3 && hallucinations.isEmpty() && trap != false
            val parts = listOfNotNull(if (consistent) 1.0 else 0.0, completeness / 4.0, if (hallucinations.isEmpty()) 1.0 else 0.0,
                trap?.let { if (it) 1.0 else 0.0 })
            JudgeResult(
                name, semantic && checks.passed, parts.average(),
                linkedMapOf("factually_consistent" to consistent, "completeness" to completeness,
                    "hallucinations" to hallucinations, "trap_handled" to trap, "semantic_pass" to semantic, "code_checks_pass" to checks.passed),
                v.path("rationale").asString(""), tracker.promptTokens, tracker.completionTokens, ms,
            )
        } catch (e: Exception) {
            log.warn("LLM judge failed on {}/{}: {}", task.id, run.arm, e.message)
            JudgeResult.failed(name, e, (System.nanoTime() - start) / 1_000_000)
        }
    }

    /** The first balanced {...} in [text]; tolerates code fences and preamble. */
    fun parseJson(text: String): JsonNode {
        val start = text.indexOf('{')
        require(start >= 0) { "judge reply has no JSON object: ${text.take(200)}" }
        var depth = 0
        var inString = false
        var escaped = false
        for (i in start until text.length) {
            val c = text[i]
            when {
                escaped -> escaped = false
                c == '\\' && inString -> escaped = true
                c == '"' -> inString = !inString
                !inString && c == '{' -> depth++
                !inString && c == '}' -> if (--depth == 0) return mapper.readTree(text.substring(start, i + 1))
            }
        }
        error("judge reply has an unterminated JSON object: ${text.take(200)}")
    }
}

/**
 * Jev as judge: atomic typed questions answered in one TypeSafe System One call, plus the code checks as
 * Jev code criteria (evaluated locally, never sent to Jev).
 */
class JevAsJudge(private val client: TypeSafeClient) : EvalJudge {

    override val name = "jev"
    private val log = LoggerFactory.getLogger(JevAsJudge::class.java)

    private val grounded = Noul.builder()
        .instructions("Is every factual claim in `assistant_answer` (versions, Maven coordinates, class and method names, " +
            "default values, behaviour) consistent with `expected_output`? Omissions are fine; contradictions or APIs " +
            "that `expected_output` does not contain are not.")
        .whenTrue("Every stated fact agrees with expected_output")
        .whenFalse("The answer states something expected_output contradicts or does not contain")
        .build()

    private val completeness = Score.builder()
        .instructions("How much of what `user_question` asks does `assistant_answer` cover correctly, judged against `expected_output`?")
        .level("None of it")
        .level("Some of it")
        .level("Most of it")
        .level("All of it")
        .build()

    private val trapHandled = Noul.builder()
        .instructions("`user_question` asks about an API that `expected_output` says does not exist. Does `assistant_answer` " +
            "clearly say that it does not exist, rather than describe how to use it?")
        .whenTrue("The answer says the API does not exist")
        .whenFalse("The answer describes or invents the non-existent API")
        .build()

    fun judgeFor(task: EvalTask, checks: CodeCheckResult): JevJudge {
        val b = JevJudge.builder(client)
            .noul("grounded", grounded, 0.7)
            .score("completeness", completeness, 2.0)
            .check("required_facts", { checks.missing.isEmpty() }, "missing required facts: ${checks.missing}")
            .check("no_invented_names", { checks.hallucinatedNames.isEmpty() }, "invented names: ${checks.hallucinatedNames}")
        if (task.trap) b.criterion(JevCriterion.noul("trap_handled", trapHandled, 0.7))
        return b.build()
    }

    override fun judge(task: EvalTask, run: RunRecord, checks: CodeCheckResult): JudgeResult {
        val start = System.nanoTime()
        return try {
            val input = JevJudgeInput.builder()
                .question(task.prompt)
                .answer(run.answer.orEmpty())
                .expected(task.reference)
                // names and arguments only: the evidence of *how* the agent worked, without megabytes of tool output
                .toolCalls(run.toolCalls.map { JevJudgeInput.ToolCall(it.name, it.arguments.take(500), null) })
                .build()
            val (verdict, ms) = timed { judgeFor(task, checks).judge(input) }
            val response = verdict.response()
            val questionNames = listOfNotNull("grounded", "completeness", if (task.trap) "trap_handled" else null)
            val values = response?.let { r ->
                questionNames.associateWith { n -> if (n == "completeness") r.scoreValue(n) / 3.0 else r.noulValue(n) }
            }.orEmpty()
            val semantic = verdict.findings()
                .filter { it.criterion() is JevCriterion.QuestionCriterion }
                .none { it.outcome() == JevFinding.Outcome.FAILED }
            JudgeResult(
                name, verdict.passed(), values.values.average().takeUnless { it.isNaN() } ?: 0.0,
                linkedMapOf<String, Any?>().apply {
                    verdict.findings().forEach { put(it.name(), it.outcome().name) }
                    values.forEach { (k, v) -> put("$k.value", "%.2f".format(v)) }
                    put("semantic_pass", semantic)
                },
                verdict.feedback().ifBlank { verdict.summary() },
                response?.usage()?.inputTokens() ?: 0, response?.usage()?.outputTokens() ?: 0, ms,
            )
        } catch (e: Exception) {
            log.warn("Jev judge failed on {}/{}: {}", task.id, run.arm, e.message)
            JudgeResult.failed(name, e, (System.nanoTime() - start) / 1_000_000)
        }
    }
}
