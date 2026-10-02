package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.model.tool.DefaultToolCallingManager
import org.springframework.ai.model.tool.ToolCallingManager
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.definition.ToolDefinition

/**
 * Per-run limits. When a limit is reached the run is not thrown away: the model is told to stop using tools
 * and answer from what it has gathered (see [TokenTrackingAdvisor]); only [hardModelCalls] aborts.
 */
data class RunBudget(
    /** Tool executions per run; beyond it each call returns an error result to the model. */
    val maxToolCalls: Int = 25,
    /** Model calls before the wrap-up turn is forced. */
    val maxModelCalls: Int = 30,
    /** Cumulative prompt tokens before the wrap-up turn is forced (each turn re-sends the whole history). */
    val maxInputTokens: Int = 250_000,
    /** Absolute stop, in case the model keeps calling tools after being told not to. */
    val hardModelCalls: Int = maxModelCalls + 3,
)

/**
 * Tool execution for one run: Spring AI's [DefaultToolCallingManager] with a total-call cap that returns an
 * error to the model instead of throwing, and a resolver for names the model mangled or invented.
 */
object RunToolCalling {

    /** Name of the tool `ToolSearchToolCallingAdvisor` adds (never capped: see [manager]). */
    const val TOOL_SEARCH_TOOL = "toolSearchTool"

    private val log = LoggerFactory.getLogger(RunToolCalling::class.java)

    /**
     * gpt-oss sometimes leaks its chat-format markers into the tool name (`symbol_to_artifact<|channel|>commentary`)
     * or prefixes a namespace (`functions.get_latest_version`). Keep the plain identifier.
     */
    fun sanitize(name: String): String = name.substringBefore("<|").trim().substringAfterLast('.').trim()

    /**
     * The budgeted tool loop for one run. The tool-call cap is applied by [capped] to the arm's own tools,
     * not by Spring AI's `maxTotalToolCalls`: that also counts `toolSearchTool`, whose "limit exceeded"
     * text the tool-search advisor then fails to parse as a JSON list (a crashed run). Tool searches stay
     * bounded by the model-call and input-token budgets.
     */
    fun manager(budget: RunBudget, callbacks: () -> List<ToolCallback>, sink: MutableList<RecordedToolCall>): ToolCallingManager =
        DefaultToolCallingManager.builder()
            .unlimitedTotalToolCalls()
            .unlimitedCallsPerTool()
            .resolutionFallbackEnabled(true)
            .toolCallbackResolver { requested ->
                val clean = sanitize(requested)
                val available = callbacks()
                available.firstOrNull { it.toolDefinition.name() == clean }
                    ?.also { log.warn("Tool name '{}' resolved to '{}'", requested, clean) }
                    ?: UnknownToolCallback(requested, available.map { it.toolDefinition.name() }, sink)
            }
            .build()

    /** Wraps a run's tools so that, together, they execute at most [maxCalls] times; later calls get an error. */
    fun capped(tools: List<ToolCallback>, maxCalls: Int): List<ToolCallback> {
        val used = java.util.concurrent.atomic.AtomicInteger()
        return tools.map { CappedToolCallback(it, used, maxCalls) }
    }
}

class CappedToolCallback(
    private val delegate: ToolCallback,
    private val used: java.util.concurrent.atomic.AtomicInteger,
    private val maxCalls: Int,
) : ToolCallback {
    override fun getToolDefinition(): ToolDefinition = delegate.toolDefinition
    override fun getToolMetadata(): org.springframework.ai.tool.metadata.ToolMetadata = delegate.toolMetadata
    override fun call(toolInput: String): String = call(toolInput, null)
    override fun call(toolInput: String, toolContext: ToolContext?): String =
        if (used.incrementAndGet() > maxCalls)
            "Error: the tool-call budget for this task ($maxCalls calls) is used up. Do not call more tools; " +
                "answer with what you have already found."
        else if (toolContext != null) delegate.call(toolInput, toolContext) else delegate.call(toolInput)
}

/** Answers a call to a tool that does not exist with an error the model can recover from. */
class UnknownToolCallback(
    private val requested: String,
    private val available: List<String>,
    private val sink: MutableList<RecordedToolCall>,
) : ToolCallback {
    override fun getToolDefinition(): ToolDefinition =
        ToolDefinition.builder().name(requested).description("unknown tool").inputSchema("{}").build()

    override fun call(toolInput: String): String {
        val msg = "Error: there is no tool named '$requested'. Available tools: ${available.ifEmpty { listOf("none") }.joinToString()}."
        sink += RecordedToolCall(requested, toolInput, null, "unknown tool")
        return msg
    }

    override fun call(toolInput: String, toolContext: ToolContext?): String = call(toolInput)
}
