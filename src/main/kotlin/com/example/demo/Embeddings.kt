package com.example.demo

import org.springframework.ai.document.Document
import org.springframework.ai.embedding.Embedding
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.embedding.EmbeddingResponse
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * Cohere Embed v3 on the Bedrock runtime (`InvokeModel`), authenticated with the same Bedrock API key as the
 * agent. Bedrock mantle has no embedding models, and Spring AI's Bedrock embedding module expects SigV4
 * credentials, so this is a small direct client.
 *
 * Cohere v3 embeds documents and queries differently: [embed] of a [Document] (what a vector store does when
 * indexing a tool) uses `search_document`; every other call (the tool-search query) uses `search_query`.
 * Input tokens come from Bedrock's `X-Amzn-Bedrock-Input-Token-Count` header and are recorded on [tracker].
 */
class BedrockCohereEmbeddingModel(
    private val apiKey: String,
    private val modelId: String = "cohere.embed-english-v3",
    private val baseUrl: String = "https://bedrock-runtime.us-east-1.amazonaws.com",
    private val tracker: TokenTracker? = null,
    /** (url, json body) -> (status, body, input tokens); replaceable in tests. */
    private val transport: (String, String) -> Triple<Int, String, Int?> = defaultTransport(apiKey),
) : EmbeddingModel {

    private val mapper = JsonMapper.builder().build()

    override fun call(request: EmbeddingRequest): EmbeddingResponse = embedAll(request.instructions, "search_query")

    override fun embed(document: Document): FloatArray =
        embedAll(listOf(getEmbeddingContent(document) ?: ""), "search_document").results.single().output

    override fun dimensions(): Int = 1024

    fun embedAll(texts: List<String>, inputType: String): EmbeddingResponse {
        val body = mapper.writeValueAsString(mapOf("texts" to texts, "input_type" to inputType, "truncate" to "END"))
        val (status, json, tokens) = transport("$baseUrl/model/$modelId/invoke", body)
        check(status in 200..299) { "Cohere embed on Bedrock failed ($status): ${json.take(300)}" }
        val vectors = mapper.readTree(json).path("embeddings")
        require(vectors.isArray && vectors.size() == texts.size) { "unexpected embed response: ${json.take(300)}" }
        tracker?.recordEmbedding(tokens ?: 0)
        return EmbeddingResponse((0 until vectors.size()).map { i ->
            val v = vectors.get(i)
            Embedding(FloatArray(v.size()) { j -> v.get(j).floatValue() }, i)
        })
    }

    companion object {
        fun defaultTransport(apiKey: String): (String, String) -> Triple<Int, String, Int?> {
            val http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build()
            return { url, body ->
                val response = http.send(
                    HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(60))
                        .header("Authorization", "Bearer $apiKey").header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                    HttpResponse.BodyHandlers.ofString(),
                )
                Triple(response.statusCode(), response.body(),
                    response.headers().firstValue("X-Amzn-Bedrock-Input-Token-Count").orElse(null)?.toIntOrNull())
            }
        }
    }
}
