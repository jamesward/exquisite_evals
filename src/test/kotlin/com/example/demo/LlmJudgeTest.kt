package com.example.demo

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.metadata.ChatResponseMetadata
import org.springframework.ai.chat.metadata.DefaultUsage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** LLM judge against a scripted model: no network. */
class LlmJudgeTest {

    private fun judgeReplying(reply: String): Pair<LlmJudge, MutableList<Prompt>> {
        val prompts = mutableListOf<Prompt>()
        val model = object : ChatModel {
            override fun call(prompt: Prompt): ChatResponse {
                prompts += prompt
                return ChatResponse(listOf(Generation(AssistantMessage(reply))),
                    ChatResponseMetadata.builder().usage(DefaultUsage(100, 20)).build())
            }
        }
        return LlmJudge(ChatClient.builder(model)) to prompts
    }

    private fun run(answer: String) = RunRecord("t", "a", answer, null, 1, 1, 1, 1, 0, 0, emptyList(), 0, emptyList())

    private val task = TaskCatalog.SPRING_AI_HOSTED_TOOL

    @Test fun `parses a fenced, chatty reply and passes a good answer`() {
        val (judge, prompts) = judgeReplying("""Sure! ```json
            {"factually_consistent": true, "completeness": 4, "hallucinations": [], "trap_handled": null, "rationale": "All six {records} listed."}
            ``` hope that helps""")
        val r = judge.judge(task, run(task.reference), CodeChecks.evaluate(task, task.reference))
        assertTrue(r.passed, r.toString())
        assertEquals(1.0, r.score)
        assertEquals(100, r.inputTokens); assertEquals(20, r.outputTokens)
        assertTrue(prompts.single().contents.contains(task.reference), "the reference is in the judge prompt")
    }

    @Test fun `code check failures fail the verdict even when the model is satisfied`() {
        val (judge, _) = judgeReplying("""{"factually_consistent": true, "completeness": 4, "hallucinations": [], "trap_handled": null, "rationale": "ok"}""")
        val answer = "2.0.1: HostedTool.WebSearch"
        val r = judge.judge(task, run(answer), CodeChecks.evaluate(task, answer))
        assertTrue(!r.passed && r.criteria["semantic_pass"] == true, r.toString())
    }

    @Test fun `hallucinations reported by the judge fail it`() {
        val (judge, _) = judgeReplying("""{"factually_consistent": false, "completeness": 3, "hallucinations": ["HostedTool.Custom"], "trap_handled": null, "rationale": "x"}""")
        val r = judge.judge(task, run(task.reference), CodeChecks.evaluate(task, task.reference))
        assertTrue(!r.passed && r.criteria["semantic_pass"] == false, r.toString())
    }

    @Test fun `an unparseable reply is a judge error, not a crash`() {
        val (judge, _) = judgeReplying("I think it's fine.")
        val r = judge.judge(task, run("x"), CodeChecks.evaluate(task, "x"))
        assertTrue(r.error != null && !r.passed)
    }
}
