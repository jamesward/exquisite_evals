package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.part.ToolCallPart
import tools.jackson.databind.json.JsonMapper

/**
 * Some models (Kimi K2.5 on Bedrock) occasionally emit a tool call whose arguments are cut off mid-JSON.
 * Spring AI runs the tool (which fails on the arguments) and then replays that assistant turn on the next
 * request, which Bedrock rejects with a 400 ("Unterminated string ..."), ending the whole run.
 *
 * Two guards: [CappedToolCallback] answers such a call with an error the model can act on, and [repair]
 * rewrites unparseable arguments in the history to `{}` before each request, so the conversation stays
 * valid. The tool result still tells the model what went wrong.
 */
object ToolCallArguments {

    private val log = LoggerFactory.getLogger(ToolCallArguments::class.java)
    private val mapper = JsonMapper.builder().build()

    /** Blank arguments are fine (Spring AI treats them as `{}`); anything else must parse as JSON. */
    fun isValid(arguments: String?): Boolean =
        arguments.isNullOrBlank() || runCatching { mapper.readTree(arguments) }.isSuccess

    fun invalidArgumentsError(toolName: String, arguments: String): String =
        "Error: the arguments for $toolName were not valid JSON (they look cut off: ${arguments.take(120)}...). " +
            "The tool did not run. Call it again with complete, valid JSON arguments."

    /** The same messages, with every assistant tool call whose arguments don't parse given `{}` instead. */
    fun repair(messages: List<Message>): List<Message> =
        messages.map { message ->
            if (message !is AssistantMessage || message.toolCalls.all { isValid(it.arguments()) }) message
            else {
                val parts = message.parts.map { part ->
                    if (part is ToolCallPart && !isValid(part.toolCall().arguments())) {
                        val call = part.toolCall()
                        log.warn("Repairing unparseable arguments of tool call {} ({}): {}", call.id(), call.name(), call.arguments().take(120))
                        ToolCallPart(AssistantMessage.ToolCall(call.id(), call.type(), call.name(), "{}"), part.payload(), part.attributes())
                    } else part
                }
                // a fresh builder: mutate() starts from the existing parts, and parts(...) appends to them
                AssistantMessage.builder().parts(parts).properties(message.metadata).build()
            }
        }
}
