package com.example.demo

import org.springaicommunity.typesafe.TypeSafeClient
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ArmCatalogTest {

    private val model = object : ChatModel {
        override fun call(prompt: Prompt): ChatResponse = error("not called")
    }

    private fun catalog(props: EvalProperties) =
        ArmCatalog(props, ChatClient.builder(model), { error("MCP must not be touched while listing arms") }, bedrockApiKey = "k")

    @Test fun `optional arms are off without configuration`() {
        val c = catalog(EvalProperties())
        assertEquals(listOf("base", "shell", "mcp", "mcp-toolsearch", "mcp-toolsearch-vector"), c.arms.map { it.id })
        assertEquals(setOf("web-brave", "web-bedrock", "mcp-toolsearch-jev"), c.disabled().keys)
        assertFailsWith<IllegalStateException> { c.arm("web-brave") }
    }

    @Test fun `a brave key enables the brave arm, blank does not`() {
        assertTrue("web-brave" in catalog(EvalProperties(braveApiKey = "x")).arms.map { it.id })
        assertTrue("web-brave" !in catalog(EvalProperties(braveApiKey = " ")).arms.map { it.id })
    }

    @Test fun `a TypeSafe client enables the jev tool-search arm`() {
        val c = ArmCatalog(EvalProperties(), ChatClient.builder(model), { error("MCP must not be touched while listing arms") },
            bedrockApiKey = "k", typeSafeClient = TypeSafeClient.builder().apiKey("k").build())
        assertEquals("mcp-toolsearch-jev", c.arms.last().id)
        assertTrue("mcp-toolsearch-jev" !in c.disabled())
    }

    @Test fun `a bedrock web search model enables the bedrock arm`() {
        val c = catalog(EvalProperties(bedrockWebSearch = EvalProperties.BedrockWebSearch(model = "openai.gpt-5.6-terra")))
        assertTrue("web-bedrock" in c.arms.map { it.id })
        assertTrue("web-bedrock" !in c.disabled())
    }
}
