package com.example.demo.spike

import com.example.demo.LoggingToolCallback
import com.example.demo.TokenTracker
import com.example.demo.TokenTrackingAdvisor
import io.modelcontextprotocol.client.McpSyncClient
import org.junit.jupiter.api.Assumptions.abort
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springaicommunity.agent.exec.LocalExecBackend
import org.springaicommunity.agent.tools.ShellTools
import org.springaicommunity.typesafe.TypeSafeClient
import org.springaicommunity.typesafe.judge.JevJudge
import org.springaicommunity.typesafe.judge.JevJudgeInput
import org.springaicommunity.typesafe.question.Noul
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.openai.responses.HostedTool
import org.springframework.ai.openai.responses.OpenAiResponsesChatModel
import org.springframework.ai.openai.responses.OpenAiResponsesChatOptions
import org.springframework.ai.openai.responses.OpenAiResponsesException
import org.springframework.ai.openai.responses.OpenAiResponsesMetadata
import org.springframework.ai.support.ToolCallbacks
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor
import org.springframework.ai.tool.toolsearch.index.lucene.LuceneToolIndex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Live spike: proves each building block of the eval demo against the real services.
 * Run with `./gradlew test -Plive --tests '*SpikeTest*'` (needs AWS_BEARER_TOKEN_BEDROCK, TYPESAFE_API_KEY).
 */
@Tag("live")
@SpringBootTest
class SpikeTest {

    @Autowired lateinit var chatModel: ChatModel
    @Autowired lateinit var chatClientBuilder: ChatClient.Builder
    @Autowired lateinit var mcpClients: List<McpSyncClient>
    @Autowired lateinit var typeSafeClient: TypeSafeClient
    @Autowired lateinit var judges: List<com.example.demo.EvalJudge>

    private val versionQuestion =
        "What is the latest released version of the Maven artifact org.springaicommunity:typesafe-spring-ai? " +
            "Use your tools to look it up; answer with just the version number."

    private fun mcpTools(sink: MutableList<com.example.demo.RecordedToolCall>) =
        LoggingToolCallback.wrap(SyncMcpToolCallbackProvider.builder().mcpClients(mcpClients).build(), sink)

    @Test
    fun `1 agent model answers and reports usage`() {
        println("SPIKE1 chatModel=${chatModel.javaClass.simpleName}")
        val tracker = TokenTracker("plain")
        val response = chatClientBuilder.build().prompt().user("Reply with exactly: pong")
            .advisors(TokenTrackingAdvisor(tracker)).call().chatResponse()!!
        val usage = response.metadata.usage
        println("SPIKE1 text='${response.result!!.output.text}' model=${response.metadata.model} usage=$usage native=${usage.nativeUsage}")
        assertTrue(response.result!!.output.text!!.contains("pong", ignoreCase = true))
        assertTrue(usage.promptTokens > 0 && usage.completionTokens > 0)
        assertEquals(1, tracker.modelCalls)
    }

    class Calculator {
        val calls = mutableListOf<String>()
        @Tool(description = "Multiply two integers exactly")
        fun multiply(a: Long, b: Long): Long = (a * b).also { calls += "$a*$b" }
    }

    @Test
    fun `2 a local tool is called through ToolCallingAdvisor and every turn is tracked`() {
        val calc = Calculator()
        val tracker = TokenTracker("local-tool")
        val answer = chatClientBuilder.build().prompt()
            .user("Use the multiply tool to compute 123456 * 789. Answer with the number only.")
            .tools(calc)
            .advisors(TokenTrackingAdvisor(tracker))
            .call().content()
        println("SPIKE2 answer=$answer calls=${calc.calls} $tracker")
        assertTrue(calc.calls.isNotEmpty(), "tool was not called")
        assertTrue(answer!!.replace(",", "").contains("97406784"))
        assertTrue(tracker.modelCalls >= 2, "tracker must see the tool-call turn and the final turn")
    }

    @Test
    fun `3 Bedrock hosted web search (GPT-5 family only)`() {
        val raw = com.example.demo.WebTools.bedrockWebSearch("low")
        // Live half: GPT-5.x family on the /openai/v1 path (the only models Bedrock web search supports).
        val model = OpenAiResponsesChatModel.builder().options(
            OpenAiResponsesChatOptions.builder()
                .baseUrl(System.getenv("WEB_SEARCH_BASE_URL") ?: "https://bedrock-mantle.us-east-1.api.aws/openai/v1")
                .apiKey(System.getenv("AWS_BEARER_TOKEN_BEDROCK"))
                .model(System.getenv("WEB_SEARCH_MODEL") ?: "openai.gpt-5.6-terra")
                .hostedTools(raw)
                .build(),
        ).build()
        val tracker = TokenTracker("web-search")
        val response = try {
            ChatClient.create(model).prompt().user(versionQuestion).advisors(TokenTrackingAdvisor(tracker)).call().chatResponse()!!
        } catch (e: Exception) {
            val msg = generateSequence<Throwable>(e) { it.cause }.joinToString(" <- ") { "${it.javaClass.simpleName}: ${it.message}" }
            if (msg.contains("access_denied") || msg.contains("not available for this account") || msg.contains("aws-marketplace:Subscribe")) abort<Unit>("BLOCKED: $msg")
            throw e
        }
        val meta = response.result!!.metadata
        println("SPIKE3 answer=${response.result!!.output.text} hosted=${meta.get<Any>(OpenAiResponsesMetadata.HOSTED_TOOL_CALLS)} " +
            "annotations=${meta.get<Any>("annotations")} usage=${response.metadata.usage}")
        assertTrue(tracker.hostedToolCalls.isNotEmpty(), "no hosted web_search_call was reported")
    }

    @Test
    fun `3b gpt-oss on v1 rejects the hosted web_search tool`() {
        val model = OpenAiResponsesChatModel.builder().options(
            OpenAiResponsesChatOptions.builder()
                .baseUrl("https://bedrock-mantle.us-east-1.api.aws/v1")
                .apiKey(System.getenv("AWS_BEARER_TOKEN_BEDROCK"))
                .model("openai.gpt-oss-120b")
                .hostedTools(HostedTool.WebSearch.of())
                .build(),
        ).build()
        val e = runCatching { ChatClient.create(model).prompt().user(versionQuestion).call().content() }.exceptionOrNull()
        val msg = generateSequence(e) { it.cause }.joinToString(" <- ") { "${it.javaClass.simpleName}: ${it.message}" }
        println("SPIKE3b error=$msg")
        assertTrue(msg.contains("web_search"), "expected a web_search rejection, got: $msg")
    }

    @Test
    fun `4a MCP tools without tool search find the latest version`() {
        val sink = LoggingToolCallback.newSink()
        val tracker = TokenTracker("mcp")
        val answer = chatClientBuilder.build().prompt().user(versionQuestion)
            .tools(*mcpTools(sink).toTypedArray())
            .advisors(TokenTrackingAdvisor(tracker))
            .call().content()
        println("SPIKE4a answer=$answer toolCalls=${sink.map { it.name }} $tracker")
        assertTrue(answer!!.contains("0.4.0"), answer)
        assertTrue(sink.isNotEmpty())
        assertTrue(tracker.toolsOfferedPerTurn.first().size >= 5, "all MCP tools are offered up front")
    }

    @Test
    fun `4b MCP tools behind ToolSearchToolCallingAdvisor with an in-process Lucene index`() {
        val sink = LoggingToolCallback.newSink()
        val tracker = TokenTracker("mcp+toolsearch")
        val advisor = ToolSearchToolCallingAdvisor.builder().toolIndex(LuceneToolIndex()).maxResults(3).build()
        val answer = chatClientBuilder.build().prompt()
            .system("You are a helpful assistant.") // the advisor appends its tool-search instructions to the system message
            .user(versionQuestion)
            .tools(*mcpTools(sink).toTypedArray())
            .advisors(advisor, TokenTrackingAdvisor(tracker))
            .advisors { it.param(ChatMemory.CONVERSATION_ID, "spike-4b-${System.nanoTime()}") }
            .call().content()
        println("SPIKE4b answer=$answer toolCalls=${sink.map { it.name }} $tracker perTurn=${tracker.toolsOfferedPerTurn}")
        assertTrue(answer!!.contains("0.4.0"), answer)
        assertEquals(1, tracker.toolsOfferedPerTurn.first().size, "only toolSearchTool is offered on turn 1")
        assertTrue(sink.isNotEmpty(), "a discovered MCP tool was executed")
    }

    @Test
    fun `5a ShellTools runs a command in a scratch directory`() {
        val dir = Files.createTempDirectory("spike-shell")
        val sink = LoggingToolCallback.newSink()
        // LocalExecBackend defaults to /bin/bash, which does not exist on NixOS; resolve bash from PATH instead.
        val shell = ShellTools.builder()
            .execBackend(LocalExecBackend.builder().workingDirectory(dir).shellCommand("/usr/bin/env", "bash", "-c").build())
            .build()
        val answer = chatClientBuilder.build().prompt()
            .user("Run this exact shell command with the Bash tool and tell me its output: echo spike-$((6*7))")
            .tools(*LoggingToolCallback.wrap(ToolCallbacks.from(shell), sink).toTypedArray())
            .advisors(ToolCallingAdvisor.builder().build())
            .call().content()
        println("SPIKE5a answer=$answer calls=$sink")
        assertTrue(sink.any { it.name == "Bash" && it.result.orEmpty().contains("spike-42") }, sink.toString())
        dir.toFile().deleteRecursively()
    }

    @Test
    fun `5b JevJudge separates a correct answer from a hallucinated one`() {
        val judge = JevJudge.builder(typeSafeClient)
            .noul("grounded", Noul.builder()
                .instructions("Is every fact in `assistant_answer` supported by `expected_output`?")
                .whenFalse("The answer states something expected_output does not support")
                .build(), 0.7)
            .check("states_version", { it.answer()?.contains("0.3.0") == true }, "does not state version 0.3.0")
            .build()
        val expected = "Latest version: 0.3.0. The judge package contains JevJudge, JevVerdict, JevFinding, JevCriterion, JevJudgeInput."
        fun verdict(answer: String) = judge.judge(JevJudgeInput.builder()
            .question("What is the latest typesafe-spring-ai version and what is in its judge package?")
            .answer(answer).expected(expected).build())
        val good = verdict("0.3.0 — it contains JevJudge, JevVerdict and JevFinding.")
        val bad = verdict("0.3.0 — it contains JevJudge and JevRubricScorer, which scores rubrics with GPT-4.")
        println("SPIKE5b good=${good.summary()} usage=${good.response()?.usage()} | bad=${bad.summary()} feedback=${bad.feedback()}")
        assertTrue(good.passed(), good.summary())
        assertTrue(!bad.passed(), bad.summary())
    }

    @Test
    fun `6 Brave WebSearch + WebFetch find the latest version`() {
        val key = System.getenv("BRAVE_API_KEY") ?: abort<Nothing>("BRAVE_API_KEY not set")
        val sink = LoggingToolCallback.newSink()
        val tracker = TokenTracker("web(brave)")
        val fetchOverhead = TokenTracker("web(brave) fetch-summaries")
        val fetchClient = chatClientBuilder.clone().defaultAdvisors(TokenTrackingAdvisor(fetchOverhead)).build()
        val answer = chatClientBuilder.build().prompt()
            .system("Today is ${java.time.LocalDate.now()}. Use web search and fetch to verify facts; do not rely on memory.")
            .user(versionQuestion)
            .tools(*com.example.demo.WebTools.braveTools(key, fetchClient, sink).toTypedArray())
            .advisors(TokenTrackingAdvisor(tracker))
            .call().content()
        println("SPIKE6 answer=$answer calls=${sink.map { it.name + ":" + it.arguments.take(120) }} $tracker | $fetchOverhead")
        assertTrue(sink.any { it.name == "WebSearch" }, "WebSearch was not called")
        assertTrue(answer!!.contains("0.4.0"), answer)
    }

    /**
     * The "coding assistant" path: no web, no MCP — just a shell and a Claude-Code-style system prompt.
     * Can the agent find the sources jar on Maven Central, download, unzip and grep its way to an API detail?
     */
    @Test
    fun `7 shell-only coding agent extracts an API detail from the sources jar`() {
        val dir = Files.createTempDirectory("spike-jar")
        val sink = LoggingToolCallback.newSink()
        val tracker = TokenTracker("shell-agent")
        val shell = ShellTools.builder()
            .execBackend(LocalExecBackend.builder().workingDirectory(dir).shellCommand("/usr/bin/env", "bash", "-c").build())
            .build()
        val system = org.springframework.core.io.ClassPathResource("prompt/MAIN_AGENT_SYSTEM_PROMPT_V2.md")
        val answer = chatClientBuilder.build().prompt()
            .system { it.text(system)
                .param(org.springaicommunity.agent.utils.AgentEnvironment.ENVIRONMENT_INFO_KEY, "Working directory: $dir\nPlatform: linux\nToday: ${java.time.LocalDate.now()}")
                .param(org.springaicommunity.agent.utils.AgentEnvironment.GIT_STATUS_KEY, "not a git repository")
                .param(org.springaicommunity.agent.utils.AgentEnvironment.AGENT_MODEL_KEY, "openai.gpt-oss-120b")
                .param(org.springaicommunity.agent.utils.AgentEnvironment.AGENT_MODEL_KNOWLEDGE_CUTOFF_KEY, "2024-06") }
            .user("In the latest release of the Maven artifact org.springaicommunity:typesafe-spring-ai, what values does the " +
                "enum JevFinding.Outcome have? Verify against the library's actual source, not memory. " +
                "Answer with the list of values and the version you checked.")
            .tools(*LoggingToolCallback.wrap(ToolCallbacks.from(shell, org.springaicommunity.agent.tools.TodoWriteTool.builder().build()), sink).toTypedArray())
            // the eval runner's tool loop: the Claude-Code-style prompt names tools (Task, Read...) this test doesn't provide
            .advisors(ToolCallingAdvisor.builder().toolCallingManager(com.example.demo.RunToolCalling.manager(com.example.demo.RunBudget(), { emptyList() }, sink)).build(),
                TokenTrackingAdvisor(tracker))
            .call().content()
        println("SPIKE7 answer=$answer")
        println("SPIKE7 commands=" + sink.map { it.arguments.replace("\n", " ").take(200) })
        println("SPIKE7 $tracker")
        dir.toFile().deleteRecursively()
        for (v in listOf("PASSED", "FAILED", "INCONCLUSIVE", "ERROR", "NOT_APPLICABLE")) assertTrue(answer!!.contains(v), "missing $v in: $answer")
    }

    @Test
    fun `8 Cohere v3 on the Bedrock runtime indexes and searches the MCP tools`() {
        val tracker = TokenTracker("embeddings")
        val model = com.example.demo.BedrockCohereEmbeddingModel(System.getenv("AWS_BEARER_TOKEN_BEDROCK"), tracker = tracker)
        val store = org.springframework.ai.vectorstore.SimpleVectorStore.builder(model).build()
        val index = org.springframework.ai.tool.toolsearch.index.vectorstore.VectorToolIndex(store)
        val tools = SyncMcpToolCallbackProvider.builder().mcpClients(mcpClients).build().toolCallbacks
        index.indexTools("s", tools.map { org.springframework.ai.tool.toolsearch.ToolReference.builder()
            .toolName(it.toolDefinition.name()).summary(it.toolDefinition.description()).build() })
        fun top(q: String) = index.search(org.springframework.ai.tool.toolsearch.ToolSearchRequest("s", q, 8, null)).toolReferences()
            .map { it.toolName() + ":" + "%.2f".format(it.relevanceScore()) }
        val queries = listOf("which Maven artifact contains this Java class?", "find the artifact for class JevJudge",
            "symbol to artifact", "latest version of a maven artifact", "read the javadoc of a class")
        queries.forEach { println("SPIKE8 '$it' -> ${top(it)}") }
        println("SPIKE8 $tracker")
        assertTrue(top(queries[0]).isNotEmpty())
        assertTrue(tracker.promptTokens > 0, "Bedrock reported input tokens")
    }

    /** The answer the old spring-ai-hostedtool reference wrongly failed; it must pass under the updated one. */
    @Test
    fun `9 both judges pass a correct stable-then-milestone answer to spring-ai-hostedtool`() {
        val task = com.example.demo.TaskCatalog.SPRING_AI_HOSTED_TOOL
        val answer = org.springframework.core.io.ClassPathResource("answers/hostedtool-stable-then-milestone.md")
            .getContentAsString(Charsets.UTF_8)
        val run = com.example.demo.RunRecord(task.id, "replay", answer, null, 0, 0, 0, 0, 0, 0, emptyList(), 0, emptyList())
        val checks = com.example.demo.CodeChecks.evaluate(task, answer)
        val llm = judges.single { it.name == "llm" }.judge(task, run, checks)  // the configured judge model
        val jev = judges.single { it.name == "jev" }.judge(task, run, checks)
        println("SPIKE9 checks=$checks\nSPIKE9 llm=${llm.passed} ${llm.score} ${llm.criteria} ${llm.rationale}\nSPIKE9 jev=${jev.passed} ${jev.score} ${jev.criteria} ${jev.rationale}")
        assertTrue(checks.passed, checks.toString())
        assertTrue(llm.passed, "LLM judge: ${llm.criteria} ${llm.rationale}")
        assertTrue(jev.passed, "Jev: ${jev.criteria} ${jev.rationale}")
    }
}
