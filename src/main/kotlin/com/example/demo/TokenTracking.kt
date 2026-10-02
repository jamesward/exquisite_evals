package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClientRequest
import org.springframework.ai.chat.client.ChatClientResponse
import org.springframework.ai.chat.client.advisor.api.AdvisorChain
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.core.Ordered

/** Mutable accumulator of token usage and per-turn tool exposure across (potentially many) model calls. */
class TokenTracker(val label: String) {
    var modelCalls: Int = 0
        private set
    var promptTokens: Int = 0
        private set
    var completionTokens: Int = 0
        private set

    /** Tool names offered to the model on each turn (shows progressive disclosure under tool search). */
    val toolsOfferedPerTurn: MutableList<List<String>> = mutableListOf()

    /** Why the model was told to stop using tools and answer, if it was. */
    var wrapUpReason: String? = null

    /** Server-executed (hosted) tool calls reported by the Responses API, e.g. web_search_call. */
    val hostedToolCalls: MutableList<Any?> = mutableListOf()

    val totalTokens: Int get() = promptTokens + completionTokens

    /** An embedding call: input tokens only. */
    @Synchronized
    fun recordEmbedding(inputTokens: Int) {
        modelCalls++
        promptTokens += inputTokens
    }

    fun record(response: ChatResponse?) {
        val usage = response?.metadata?.usage ?: return
        modelCalls++
        promptTokens += usage.promptTokens
        completionTokens += usage.completionTokens
    }

    override fun toString() =
        "$label: calls=$modelCalls in=$promptTokens out=$completionTokens tools/turn=${toolsOfferedPerTurn.map { it.size }}"
}

/**
 * Accumulates the usage of every model call it sees. Ordered just before the terminal model-call advisor
 * (which is at [Ordered.LOWEST_PRECEDENCE]; a tie would sort this advisor after it and it would never run),
 * so it sits innermost — inside the tool-calling loop — and runs once per model call.
 */
class TokenTrackingAdvisor(private val tracker: TokenTracker, private val budget: RunBudget? = null) : BaseAdvisor {

    private val log = LoggerFactory.getLogger(TokenTrackingAdvisor::class.java)

    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE - 1

    override fun before(chatClientRequest: ChatClientRequest, advisorChain: AdvisorChain): ChatClientRequest {
        val request = budget?.let { enforce(it, repairToolCalls(chatClientRequest)) } ?: chatClientRequest
        val options = request.prompt().options as? ToolCallingChatOptions
        tracker.toolsOfferedPerTurn += options?.toolCallbacks?.map { it.toolDefinition.name() }.orEmpty()
        return request
    }

    /** Replace tool-call arguments that aren't valid JSON, which Bedrock would reject on replay (see [ToolCallArguments]). */
    private fun repairToolCalls(request: ChatClientRequest): ChatClientRequest {
        val messages = request.prompt().instructions
        val repaired = ToolCallArguments.repair(messages)
        if (repaired.indices.all { repaired[it] === messages[it] }) return request
        return request.mutate().prompt(request.prompt().mutate().messages(repaired).build()).build()
    }

    /**
     * ToolCallingAdvisor has no iteration cap. Past the soft budget, remove the tools and ask for the final
     * answer, so an agent that goes off the rails still returns what it found (and is graded on it); past
     * the hard cap, stop.
     */
    private fun enforce(budget: RunBudget, request: ChatClientRequest): ChatClientRequest {
        if (tracker.modelCalls >= budget.hardModelCalls) throw TurnLimitExceededException(tracker.label, budget.hardModelCalls)
        val reason = tracker.wrapUpReason
            ?: when {
                tracker.modelCalls >= budget.maxModelCalls -> "reached ${budget.maxModelCalls} model calls"
                tracker.promptTokens >= budget.maxInputTokens -> "used ${tracker.promptTokens} input tokens (budget ${budget.maxInputTokens})"
                else -> null
            }?.also { tracker.wrapUpReason = it; log.warn("[{}] {}: forcing a final answer without tools", tracker.label, it) }
            ?: return request
        val options = request.prompt().options as? ToolCallingChatOptions ?: return request
        val noTools = options.mutate().toolCallbacks(emptyList()).build()
        val messages = request.prompt().instructions + UserMessage(
            "Your tool budget is used up ($reason). Do not call any more tools. Answer the original question now using " +
                "only what you have already found, and say clearly which parts you could not verify.")
        return request.mutate().prompt(Prompt.builder().messages(messages).chatOptions(noTools).build()).build()
    }

    override fun after(chatClientResponse: ChatClientResponse, advisorChain: AdvisorChain): ChatClientResponse {
        val response = chatClientResponse.chatResponse()
        val usage = response?.metadata?.usage
        if (usage != null) {
            tracker.record(response)
            response.results.forEach { g ->
                when (val hosted = g.metadata.get<Any>(HOSTED_TOOL_CALLS)) {
                    null -> Unit
                    is Collection<*> -> tracker.hostedToolCalls.addAll(hosted) // one entry per web_search_call etc.
                    else -> tracker.hostedToolCalls.add(hosted)
                }
            }
            log.info(
                "[{}] turn {}: prompt={} completion={} tools offered={}",
                tracker.label, tracker.modelCalls, usage.promptTokens, usage.completionTokens,
                tracker.toolsOfferedPerTurn.lastOrNull(),
            )
        }
        return chatClientResponse
    }

    companion object {
        /** Mirrors OpenAiResponsesMetadata.HOSTED_TOOL_CALLS without a hard dependency on it here. */
        const val HOSTED_TOOL_CALLS = "openai.responses.hosted_tool_calls"
    }
}

class TurnLimitExceededException(label: String, limit: Int) :
    IllegalStateException("[$label] stopped after $limit model calls (turn limit)")
