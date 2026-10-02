package com.example.demo

import org.springframework.ai.document.Document
import tools.jackson.databind.json.JsonMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EmbeddingsTest {

    private val mapper = JsonMapper.builder().build()
    private val requests = mutableListOf<Pair<String, String>>()

    private fun model(tracker: TokenTracker? = null, status: Int = 200) = BedrockCohereEmbeddingModel("k", tracker = tracker,
        transport = { url, body ->
            requests += url to body
            val n = mapper.readTree(body).path("texts").size()
            Triple(status, mapper.writeValueAsString(mapOf("embeddings" to List(n) { i -> listOf(i + 0.5, 1.0, 2.0) })), 7)
        })

    private fun inputType() = mapper.readTree(requests.last().second).path("input_type").asString()

    @Test fun `documents are embedded as search_document, queries as search_query`() {
        val m = model()
        assertEquals(listOf(0.5f, 1f, 2f), m.embed(Document("resolve the latest version")).toList())
        assertEquals("search_document", inputType())
        m.embed("which tool finds a class?")
        assertEquals("search_query", inputType())
        assertEquals("https://bedrock-runtime.us-east-1.amazonaws.com/model/cohere.embed-english-v3/invoke", requests.last().first)
    }

    @Test fun `input tokens are recorded as in-tool usage`() {
        val t = TokenTracker("in-tool")
        model(t).embed(listOf("a", "b"))
        assertEquals(1, t.modelCalls); assertEquals(7, t.promptTokens); assertEquals(0, t.completionTokens)
    }

    @Test fun `an error status fails loudly`() {
        assertFailsWith<IllegalStateException> { model(status = 403).embed("q") }
    }
}
