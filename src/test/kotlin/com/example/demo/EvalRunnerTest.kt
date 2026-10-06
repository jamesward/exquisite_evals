package com.example.demo

import org.springaicommunity.typesafe.TypeSafeClient
import org.springframework.ai.chat.client.ChatClient
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
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** What actually reaches the model for each arm — offline, with a scripted model and a fake MCP tool. */
class EvalRunnerTest {

    private val seen = mutableListOf<Prompt>()
    private val model = object : ChatModel {
        // ChatClient only attaches tools when the model's default options are tool-calling options
        override fun getOptions() = ToolCallingChatOptions.builder().build()
        override fun call(prompt: Prompt): ChatResponse {
            seen += prompt
            return ChatResponse(listOf(Generation(AssistantMessage("0.3.0"))), ChatResponseMetadata.builder().usage(DefaultUsage(10, 2)).build())
        }
    }
    private val fakeMcpTool = FunctionToolCallback.builder("get_latest_version", java.util.function.Function<Map<String, Any>, String> { "0.3.0" })
        .description("latest version").inputType(Map::class.java).build()

    /** Offline stand-in for Cohere: a constant vector, so every tool matches every query. */
    private val fakeEmbeddings = { t: TokenTracker ->
        BedrockCohereEmbeddingModel("k", tracker = t, transport = { _, body ->
            val n = tools.jackson.databind.json.JsonMapper.builder().build().readTree(body).path("texts").size()
            Triple(200, "{\"embeddings\": [" + List(n) { "[1.0, 0.0]" }.joinToString() + "]}", 3)
        })
    }
    // the Jev index only calls TypeSafe when the model searches, which the fake model never does
    private val catalog = ArmCatalog(EvalProperties(), ChatClient.builder(model), { listOf(fakeMcpTool) }, bedrockApiKey = "k", embeddings = fakeEmbeddings,
        typeSafeClient = TypeSafeClient.builder().apiKey("k").build())
    private val runner = EvalRunner(catalog, emptyList(), outputRoot = Files.createTempDirectory("evals-test"))

    private fun toolNames(p: Prompt) = (p.options as? ToolCallingChatOptions)?.toolCallbacks?.map { it.toolDefinition.name() }.orEmpty()

    @Test fun `every arm sends the shared system prompt and the task`() {
        catalog.arms.forEach { arm ->
            seen.clear()
            val r = runner.execute(TaskCatalog.JEVJUDGE_GAV, arm)
            assertEquals(null, r.error, arm.id)
            val first = seen.first()
            // the tool-search advisor appends its own instructions, so the shared prompt is a prefix
            val system = first.instructions.first { it.messageType == MessageType.SYSTEM }.text.orEmpty()
            assertTrue(system.startsWith(runner.systemPrompt), arm.id)
            assertTrue(first.instructions.any { it.messageType == MessageType.USER && it.text == TaskCatalog.JEVJUDGE_GAV.prompt }, arm.id)
            assertEquals(1, r.modelCalls, arm.id); assertEquals(0, r.hostedToolCalls, arm.id)
            // indexing the tools is an in-tool model call for the vector arm only
            assertEquals(if (arm.id == "mcp-toolsearch-vector") 3 else 0, r.overheadInputTokens, arm.id)
        }
    }

    @Test fun `tools per arm - base none, mcp all up front, tool search only its search tool`() {
        fun offered(armId: String): List<String> { seen.clear(); runner.execute(TaskCatalog.JEVJUDGE_GAV, catalog.arm(armId)); return toolNames(seen.first()) }
        assertEquals(emptyList(), offered("base"))
        assertEquals(listOf("get_latest_version"), offered("mcp"))
        assertEquals(listOf("toolSearchTool"), offered("mcp-toolsearch"))
        assertEquals(listOf("toolSearchTool"), offered("mcp-toolsearch-vector"))
        assertEquals(listOf("toolSearchTool"), offered("mcp-toolsearch-jev"))
        assertTrue(offered("shell").containsAll(listOf("Bash", "TodoWrite")))
    }
}
