package com.example.demo

import org.springaicommunity.agent.exec.LocalExecBackend
import org.springaicommunity.agent.tools.ShellTools
import org.springaicommunity.agent.tools.TodoWriteTool
import org.springaicommunity.typesafe.TypeSafeClient
import org.springaicommunity.typesafe.toolsearch.JevToolIndex
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.ai.model.tool.ToolCallingManager
import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor
import org.springframework.ai.openai.responses.OpenAiResponsesChatModel
import org.springframework.ai.openai.responses.OpenAiResponsesChatOptions
import org.springframework.ai.support.ToolCallbacks
import org.springframework.ai.tool.ToolCallback
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.tool.toolsearch.index.lucene.LuceneToolIndex
import org.springframework.ai.tool.toolsearch.index.vectorstore.VectorToolIndex
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path

/** `evals.*` settings. Optional arms are enabled only when their settings are present. */
@ConfigurationProperties("evals")
data class EvalProperties(
    /** Brave Search API key; the web-brave arm exists only when this is set. */
    val braveApiKey: String? = null,
    /** Bedrock server-side web search (GPT-5.x on mantle /openai/v1); the web-bedrock arm exists only when [model] is set. */
    val bedrockWebSearch: BedrockWebSearch = BedrockWebSearch(),
    /** Embedding model for the mcp-toolsearch-vector arm (Bedrock runtime; mantle has no embedding models). */
    val embedding: Embedding = Embedding(),
    /** Where the shell arms run model-written commands. */
    val sandbox: Sandbox = Sandbox(),
) {
    /**
     * [mode] `docker` (default) runs commands in a fresh container from [image] per run, with only the run's
     * scratch directory mounted and none of the host's environment (so no API keys); `local` runs them on the
     * host, inheriting its environment, as the runs before 2026-10-02 did.
     */
    data class Sandbox(
        val mode: String = "docker",
        val image: String = "exquisite-evals-sandbox:1",
    )

    data class Embedding(
        val model: String = "cohere.embed-english-v3",
        val baseUrl: String = "https://bedrock-runtime.us-east-1.amazonaws.com",
    )

    data class BedrockWebSearch(
        val model: String? = null,
        val baseUrl: String = "https://bedrock-mantle.us-east-1.api.aws/openai/v1",
        val contextSize: String = "medium",
    )
}

/** Per-execution state an arm's tools write into: recorded tool calls, overhead tokens, a working dir. */
class ArmRun(
    val sessionId: String,
    val workDir: Path,
    val toolCalls: MutableList<RecordedToolCall> = LoggingToolCallback.newSink(),
    /** Model calls made inside tools (e.g. WebFetch page summaries) — invisible to the agent's tracker. */
    val overhead: TokenTracker = TokenTracker("$sessionId (in-tool)"),
) : AutoCloseable {
    private val resources = mutableListOf<AutoCloseable>()

    /** Registers something to release when the run ends (e.g. its sandbox container). */
    fun <T : AutoCloseable> own(resource: T): T = resource.also { resources += it }

    override fun close() {
        resources.asReversed().forEach { runCatching { it.close() } }
    }
}

/**
 * One experimental condition. Every arm shares the agent model and system prompt; they differ only in
 * [client] (normally the shared builder — the Bedrock arm needs its own model), [tools] and [toolAdvisor]
 * (the tool loop; given the run's budgeted [ToolCallingManager]).
 */
class Arm(
    val id: String,
    val description: String,
    val client: () -> ChatClient.Builder,
    val tools: (ArmRun) -> List<ToolCallback> = { emptyList() },
    val toolAdvisor: (ArmRun, ToolCallingManager) -> Advisor = { _, m -> ToolCallingAdvisor.builder().toolCallingManager(m).build() },
)

/**
 * Builds the set of arms. [mcpTools] is lazy so that arms which never touch MCP don't need a connection.
 * Add new arms here; optional ones return null from their factory when unconfigured.
 */
class ArmCatalog(
    private val props: EvalProperties,
    private val agentClient: ChatClient.Builder,
    private val mcpTools: () -> List<ToolCallback>,
    private val bedrockApiKey: String? = System.getenv("AWS_BEARER_TOKEN_BEDROCK"),
    /** Embedding model per run; its calls are recorded on the run's in-tool tracker. */
    private val embeddings: (TokenTracker) -> EmbeddingModel = { tracker ->
        BedrockCohereEmbeddingModel(bedrockApiKey.orEmpty(), props.embedding.model, props.embedding.baseUrl, tracker)
    },
    /** TypeSafe client for the mcp-toolsearch-jev arm; the arm exists only when this is set. */
    private val typeSafeClient: TypeSafeClient? = null,
) {

    val arms: List<Arm> by lazy {
        listOfNotNull(base(), shell(), webBrave(), webBedrock(), mcp(), mcpToolSearch(), mcpToolSearchVector(), mcpToolSearchJev())
    }

    fun arm(id: String): Arm = arms.firstOrNull { it.id == id }
        ?: error("unknown or disabled arm '$id'; enabled: ${arms.map { it.id }}")

    /** Arms that were requested but are disabled, with the reason — for the report. */
    fun disabled(): Map<String, String> = buildMap {
        if (props.braveApiKey.isNullOrBlank()) put("web-brave", "set BRAVE_API_KEY (evals.brave-api-key)")
        if (props.bedrockWebSearch.model.isNullOrBlank()) put("web-bedrock", "set evals.bedrock-web-search.model to a GPT-5.x model your account can use")
        if (typeSafeClient == null) put("mcp-toolsearch-jev", "set TYPESAFE_API_KEY (spring.ai.typesafe.api-key)")
    }

    private fun base() = Arm("base", "no tools: parametric knowledge only", { agentClient.clone() })

    private fun shell() = Arm("shell", "shell + todo: find, download, extract and grep jars like a coding assistant",
        { agentClient.clone() },
        tools = { run -> wrap(run, shellAndTodo(run)) })

    private fun webBrave(): Arm? {
        val key = props.braveApiKey?.takeIf { it.isNotBlank() } ?: return null
        return Arm("web-brave", "Brave WebSearch + WebFetch + shell", { agentClient.clone() },
            tools = { run ->
                val fetchClient = agentClient.clone().defaultAdvisors(TokenTrackingAdvisor(run.overhead)).build()
                WebTools.braveTools(key, fetchClient, run.toolCalls) + wrap(run, shellAndTodo(run))
            })
    }

    private fun webBedrock(): Arm? {
        val cfg = props.bedrockWebSearch
        val model = cfg.model?.takeIf { it.isNotBlank() } ?: return null
        return Arm("web-bedrock", "Bedrock server-side web_search ($model) + shell", {
            ChatClient.builder(OpenAiResponsesChatModel.builder().options(
                OpenAiResponsesChatOptions.builder().baseUrl(cfg.baseUrl).apiKey(bedrockApiKey).model(model)
                    .hostedTools(WebTools.bedrockWebSearch(cfg.contextSize)).build(),
            ).build())
        }, tools = { run -> wrap(run, shellAndTodo(run)) })
    }

    private fun mcp() = Arm("mcp", "javadocs.dev MCP tools, all offered up front", { agentClient.clone() },
        tools = { run -> mcpTools().map { LoggingToolCallback(it, run.toolCalls) } })

    private fun mcpToolSearch() = Arm("mcp-toolsearch", "javadocs.dev MCP tools behind ToolSearchToolCallingAdvisor (in-process Lucene)",
        { agentClient.clone() },
        tools = { run -> mcpTools().map { LoggingToolCallback(it, run.toolCalls) } },
        toolAdvisor = { _, m -> ToolSearchToolCallingAdvisor.builder().toolCallingManager(m).toolIndex(LuceneToolIndex()).maxResults(5).build() })

    /** Same, but semantic: tool descriptions and search queries embedded with Cohere v3 into an in-memory vector store. */
    private fun mcpToolSearchVector() = Arm("mcp-toolsearch-vector",
        "javadocs.dev MCP tools behind ToolSearchToolCallingAdvisor (Cohere v3 embeddings, in-memory SimpleVectorStore)",
        { agentClient.clone() },
        tools = { run -> mcpTools().map { LoggingToolCallback(it, run.toolCalls) } },
        toolAdvisor = { run, m ->
            val store = SimpleVectorStore.builder(embeddings(run.overhead)).build()
            ToolSearchToolCallingAdvisor.builder().toolCallingManager(m).toolIndex(VectorToolIndex(store)).maxResults(5).build()
        })

    /**
     * Same, but TypeSafe's Jev picks the tools: one call per search judges which tools perform the requested task.
     * Unlike Lucene and the vector store, it can also answer "no tool applies". Its calls aren't counted as
     * in-tool tokens: JevToolIndex doesn't expose their usage.
     */
    private fun mcpToolSearchJev(): Arm? {
        val client = typeSafeClient ?: return null
        return Arm("mcp-toolsearch-jev", "javadocs.dev MCP tools behind ToolSearchToolCallingAdvisor (TypeSafe Jev picks the tools)",
            { agentClient.clone() },
            tools = { run -> mcpTools().map { LoggingToolCallback(it, run.toolCalls) } },
            toolAdvisor = { _, m ->
                val index = JevToolIndex.builder(client)
                    .applicabilityThreshold(0.5) // below this, no tool is returned at all
                    .minimumRelevance(0.05) // don't pad maxResults with tools Jev ruled out
                    .build()
                ToolSearchToolCallingAdvisor.builder().toolCallingManager(m).toolIndex(index).maxResults(5).build()
            })
    }

    private fun shellAndTodo(run: ArmRun): List<ToolCallback> {
        val backend = when (props.sandbox.mode) {
            "local" ->
                // On the host, but without the JVM's environment (API keys): only PATH and HOME are passed through.
                // LocalExecBackend defaults to /bin/bash, absent on NixOS: resolve bash from PATH.
                LocalExecBackend.builder().workingDirectory(run.workDir).shellCommand("/usr/bin/env", "bash", "-c")
                    .cleanEnvironment(true)
                    .environment(listOf("PATH", "HOME").mapNotNull { k -> System.getenv(k)?.let { k to it } }.toMap())
                    .build()
            "docker" -> run.own(Sandboxes.docker(props.sandbox.image, run.workDir))
            else -> error("evals.sandbox.mode must be docker or local, was '${props.sandbox.mode}'")
        }
        return ToolCallbacks.from(ShellTools.builder().execBackend(backend).build(), TodoWriteTool.builder().build()).toList()
    }

    private fun wrap(run: ArmRun, cbs: List<ToolCallback>) = cbs.map { LoggingToolCallback(it, run.toolCalls) }
}
