package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.definition.ToolDefinition
import org.springframework.ai.tool.metadata.ToolMetadata
import java.util.Collections

/** One executed tool call, as a judge (e.g. Jev's `tool_calls` state) needs it. */
data class RecordedToolCall(val name: String, val arguments: String, val result: String?, val error: String? = null)

/**
 * Decorates a [ToolCallback] to log each invocation and record (name, input, output) into [sink].
 * Log lines are truncated so a huge payload doesn't flood the console; the recording is not.
 */
class LoggingToolCallback(
    private val delegate: ToolCallback,
    private val sink: MutableList<RecordedToolCall>? = null,
) : ToolCallback {

    private val log = LoggerFactory.getLogger(LoggingToolCallback::class.java)

    override fun getToolDefinition(): ToolDefinition = delegate.toolDefinition

    override fun getToolMetadata(): ToolMetadata = delegate.toolMetadata

    override fun call(toolInput: String): String = logged(toolInput) { delegate.call(toolInput) }

    override fun call(toolInput: String, toolContext: ToolContext?): String =
        logged(toolInput) { delegate.call(toolInput, toolContext) }

    private inline fun logged(toolInput: String, invoke: () -> String): String {
        val name = delegate.toolDefinition.name()
        log.info("Tool call -> {} input={}", name, truncate(toolInput))
        try {
            val output = invoke()
            log.info("Tool call <- {} output={}", name, truncate(output))
            sink?.add(RecordedToolCall(name, toolInput, output))
            return output
        } catch (e: Exception) {
            log.error("Tool call !! {} failed: {}", name, e.message)
            sink?.add(RecordedToolCall(name, toolInput, null, e.message ?: e.javaClass.simpleName))
            throw e
        }
    }

    private fun truncate(s: String): String {
        val oneLine = s.replace('\n', ' ')
        return if (oneLine.length <= MAX_LOG_CHARS) oneLine else oneLine.take(MAX_LOG_CHARS) + "…[truncated, ${s.length} chars total]"
    }

    companion object {
        private const val MAX_LOG_CHARS = 2000

        /** Wraps every tool of [provider]; recorded calls go to [sink] when given. */
        fun wrap(provider: ToolCallbackProvider, sink: MutableList<RecordedToolCall>? = null): List<ToolCallback> =
            provider.toolCallbacks.map { LoggingToolCallback(it, sink) }

        /** Wraps plain callbacks (e.g. from `ToolCallbacks.from(bean)`). */
        fun wrap(callbacks: Array<ToolCallback>, sink: MutableList<RecordedToolCall>? = null): List<ToolCallback> =
            callbacks.map { LoggingToolCallback(it, sink) }

        fun newSink(): MutableList<RecordedToolCall> = Collections.synchronizedList(mutableListOf())
    }
}
