package com.example.demo.diag

import io.modelcontextprotocol.client.McpSyncClient
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.tool.toolsearch.ToolReference
import org.springframework.ai.tool.toolsearch.ToolSearchRequest
import org.springframework.ai.tool.toolsearch.index.lucene.LuceneToolIndex
import org.springframework.ai.tool.toolsearch.index.vectorstore.VectorToolIndex
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/** Diagnostics for the tool-search arms: how big the MCP tool definitions are, and how each index ranks typical queries. */
@Tag("live")
@SpringBootTest
class ToolSearchDiagnosticsTest {
    @Autowired lateinit var mcpClients: List<McpSyncClient>

    @Test fun `tool definition sizes and search rankings`() {
        val tools = SyncMcpToolCallbackProvider.builder().mcpClients(mcpClients).build().toolCallbacks
        tools.forEach { val d = it.toolDefinition
            println("DIAG size ${d.name()} desc=${d.description().length} schema=${d.inputSchema().length} chars") }
        println("DIAG total chars=${tools.sumOf { it.toolDefinition.let { d -> d.name().length + d.description().length + d.inputSchema().length } }}")
        val refs = tools.map { ToolReference.builder().toolName(it.toolDefinition.name()).summary(it.toolDefinition.description()).build() }
        val lucene = LuceneToolIndex().also { it.indexTools("s", refs) }
        val luceneAll = LuceneToolIndex(0f).also { it.indexTools("s", refs) }
        val vector = VectorToolIndex(SimpleVectorStore.builder(
            com.example.demo.BedrockCohereEmbeddingModel(System.getenv("AWS_BEARER_TOKEN_BEDROCK"))).build()).also { it.indexTools("s", refs) }
        val queries = listOf(
            "maven", "maven artifact", "find maven artifact", "latest version", "get latest version of maven artifact",
            "find artifact containing class", "search artifacts by name", "which library contains class JevJudge",
            "javadoc", "read class documentation", "list classes in library", "read source code", "spring-ai-agent-utils LocalExecBackend",
            "find Maven coordinates for spring-ai-agent-utils", "Maven Central search", "pre-release milestone version")
        fun fmt(i: org.springframework.ai.tool.toolsearch.ToolIndex, q: String, n: Int) =
            i.search(ToolSearchRequest("s", q, n, null)).toolReferences().joinToString { "${it.toolName()}:%.2f".format(it.relevanceScore()) }
        queries.forEach { q ->
            println("DIAG q='$q'\n  lucene(min .25,top5)=[${fmt(lucene, q, 5)}]\n  lucene(all)=[${fmt(luceneAll, q, 8)}]\n  vector(top5)=[${fmt(vector, q, 5)}]")
        }
    }

    /** Replays the queries agents actually sent (from the toolsearch-diag runs) against the keyword index. */
    @Test fun `replay recorded tool searches`() {
        val tools = SyncMcpToolCallbackProvider.builder().mcpClients(mcpClients).build().toolCallbacks
        val refs = tools.map { ToolReference.builder().toolName(it.toolDefinition.name()).summary(it.toolDefinition.description()).build() }
        val lucene = LuceneToolIndex().also { it.indexTools("s", refs) }
        val luceneAll = LuceneToolIndex(0f).also { it.indexTools("s", refs) }
        val mapper = com.fasterxml.jackson.databind.ObjectMapper()
        val queries = java.io.File("results").listFiles { f -> f.name.contains("toolsearch-diag") }.orEmpty()
            .flatMap { d -> mapper.readTree(d.resolve("results.json")).flatMap { r -> r["run"]["toolSearches"] ?: emptyList() } }
            .map { mapper.readTree(it["arguments"].asText())["query"].asText() }.distinct()
        var empty = 0; var emptyAll = 0
        queries.forEach { q ->
            val a = lucene.search(ToolSearchRequest("s", q, 5, null)).toolReferences().map { it.toolName() }
            val b = luceneAll.search(ToolSearchRequest("s", q, 5, null)).toolReferences().map { it.toolName() }
            if (a.isEmpty()) empty++; if (b.isEmpty()) emptyAll++
            println("REPLAY '$q'\n   min.25=$a\n   min0=$b")
        }
        println("REPLAY ${queries.size} queries: empty at min 0.25=$empty, at min 0=$emptyAll")
    }
}
