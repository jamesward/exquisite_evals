package com.example.demo

import org.springaicommunity.agent.tools.BraveWebSearchTool
import org.springaicommunity.agent.tools.SmartWebFetchTool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.responses.HostedTool
import org.springframework.ai.support.ToolCallbacks
import org.springframework.ai.tool.ToolCallback

/**
 * Web access for the web arms, in two interchangeable flavours:
 *  - [braveTools]: client-side WebSearch (Brave) + WebFetch (agent-utils), works with any tool-calling model.
 *  - [bedrockWebSearch]: Bedrock's server-side web_search, only for GPT-5.x models on the mantle /openai/v1 path.
 */
object WebTools {

    /**
     * Brave search + fetch. WebFetch summarizes each page with [fetchClient]'s model; pass a client whose
     * advisors track tokens (the "overhead" column), since those calls never reach the agent's tracker.
     */
    fun braveTools(braveApiKey: String, fetchClient: ChatClient, sink: MutableList<RecordedToolCall>? = null): List<ToolCallback> {
        val search = BraveWebSearchTool.builder(braveApiKey).resultCount(8).build()
        // domainSafetyCheck would send every fetched domain to a third-party (Claude) API; keep it off.
        val fetch = SmartWebFetchTool.builder(fetchClient).domainSafetyCheck(false).maxContentLength(60_000).build()
        return ToolCallbacks.from(search, fetch).map { LoggingToolCallback(it, sink) }
    }

    /** Bedrock server-side web search; external_web_access=false keeps fetches inside the AWS boundary. */
    fun bedrockWebSearch(contextSize: String = "medium"): HostedTool =
        HostedTool.Raw(mapOf("type" to "web_search", "external_web_access" to false, "search_context_size" to contextSize))
}
