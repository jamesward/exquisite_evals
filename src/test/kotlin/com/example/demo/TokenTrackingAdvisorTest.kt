package com.example.demo

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.metadata.ChatResponseMetadata
import org.springframework.ai.chat.metadata.DefaultUsage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.tool.ToolCallingChatOptions
import org.springframework.ai.tool.function.FunctionToolCallback
import java.util.function.Function
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Budget enforcement against a scripted model that calls a tool whenever tools are offered. */
class TokenTrackingAdvisorTest {

    private val prompts = mutableListOf<Prompt>()

    /** Calls [toolName] while tools are offered (ignoring instructions, like a runaway agent); answers once they are removed. */
    private fun model(toolName: String = "echo", ignoreWrapUp: Boolean = false) = object : ChatModel {
        override fun getOptions() = ToolCallingChatOptions.builder().build()
        override fun call(prompt: Prompt): ChatResponse {
            prompts += prompt
            val offered = (prompt.options as? ToolCallingChatOptions)?.toolCallbacks.orEmpty()
            val msg = if (offered.isNotEmpty() || ignoreWrapUp)
                AssistantMessage.builder().content("").toolCalls(listOf(AssistantMessage.ToolCall("c${prompts.size}", "function", toolName, "{}"))).build()
            else AssistantMessage("final answer")
            return ChatResponse(listOf(Generation(msg)), ChatResponseMetadata.builder().usage(DefaultUsage(1000, 10)).build())
        }
    }

    private val sink = LoggingToolCallback.newSink()
    private val echo = LoggingToolCallback(FunctionToolCallback.builder("echo", Function<Map<String, Any>, String> { "pong" })
        .description("echo").inputType(Map::class.java).build(), sink)

    private fun run(model: ChatModel, budget: RunBudget, tracker: TokenTracker = TokenTracker("t")): Pair<String?, TokenTracker> {
        val tools = RunToolCalling.capped(listOf(echo), budget.maxToolCalls)
        val answer = ChatClient.builder(model).build().prompt().user("q").tools(*tools.toTypedArray())
            .advisors(ToolCallingAdvisor.builder().toolCallingManager(RunToolCalling.manager(budget, { tools }, sink)).build(),
                TokenTrackingAdvisor(tracker, budget))
            .call().content()
        return answer to tracker
    }

    @Test fun `tracks usage on a plain client with no tools`() {
        val t = TokenTracker("plain")
        ChatClient.builder(model()).build().prompt().user("q").advisors(TokenTrackingAdvisor(t)).call().content()
        assertEquals(1, t.modelCalls); assertEquals(1000, t.promptTokens); assertEquals(10, t.completionTokens)
    }

    @Test fun `a runaway tool loop is wrapped up with a tool-free final answer`() {
        val (answer, t) = run(model(), RunBudget(maxToolCalls = 100, maxModelCalls = 3))
        assertEquals("final answer", answer)
        assertEquals(4, t.modelCalls, "3 tool turns + the forced wrap-up")
        assertNotNull(t.wrapUpReason)
        val last = prompts.last()
        assertTrue((last.options as ToolCallingChatOptions).toolCallbacks.isNullOrEmpty(), "no tools offered on the wrap-up turn")
        assertTrue(last.instructions.last { it.messageType == MessageType.USER }.text!!.contains("tool budget is used up"))
    }

    @Test fun `the input-token budget also triggers the wrap-up`() {
        val (answer, t) = run(model(), RunBudget(maxToolCalls = 100, maxModelCalls = 100, maxInputTokens = 2500))
        assertEquals("final answer", answer)
        assertTrue(t.wrapUpReason!!.contains("input tokens"), t.wrapUpReason)
    }

    @Test fun `a model that ignores the wrap-up is stopped at the hard cap`() {
        assertFailsWith<TurnLimitExceededException> { run(model(ignoreWrapUp = true), RunBudget(maxToolCalls = 100, maxModelCalls = 2, hardModelCalls = 4)) }
    }

    @Test fun `tool calls past the limit return an error to the model instead of running`() {
        run(model(), RunBudget(maxToolCalls = 2, maxModelCalls = 5))
        assertEquals(2, sink.size, "only 2 executions; later calls got an error result")
    }

    @Test fun `a mangled tool name is resolved and an unknown one is answered with an error`() {
        val (a1, _) = run(model("echo<|channel|>commentary"), RunBudget(maxToolCalls = 1, maxModelCalls = 1))
        assertEquals("final answer", a1)
        assertEquals("echo", sink.single().name, "the mangled name ran the real tool")
        sink.clear()
        val (a2, _) = run(model("find"), RunBudget(maxToolCalls = 5, maxModelCalls = 1))
        assertEquals("final answer", a2)
        assertEquals("unknown tool", sink.single().error)
        assertEquals("find", RunToolCalling.sanitize("functions.find<|end|><|start|>assistant"))
    }

    @Test fun `toolSearchTool is not cut off by the tool-call cap`() {
        val search = LoggingToolCallback(FunctionToolCallback.builder(RunToolCalling.TOOL_SEARCH_TOOL, Function<Map<String, Any>, String> { "[\"echo\"]" })
            .description("search").inputType(Map::class.java).build(), sink)
        val manager = RunToolCalling.manager(RunBudget(maxToolCalls = 1, maxModelCalls = 4), { listOf(search) }, sink)
        ChatClient.builder(model(RunToolCalling.TOOL_SEARCH_TOOL)).build().prompt().user("q").tools(search)
            .advisors(ToolCallingAdvisor.builder().toolCallingManager(manager).build(),
                TokenTrackingAdvisor(TokenTracker("t"), RunBudget(maxToolCalls = 1, maxModelCalls = 4)))
            .call().content()
        assertTrue(sink.size >= 3 && sink.all { it.result == "[\"echo\"]" }, "every search ran, none got the limit error: $sink")
    }
}
