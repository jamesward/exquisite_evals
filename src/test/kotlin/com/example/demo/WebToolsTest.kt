package com.example.demo

import kotlin.test.Test
import kotlin.test.assertTrue

class WebToolsTest {
    @Test fun `bedrock web search tool keeps external_web_access=false on the wire`() {
        val json = com.openai.core.jsonMapper().writeValueAsString(WebTools.bedrockWebSearch("low").toTool())
        assertTrue(json.contains("\"type\":\"web_search\"") && json.contains("\"external_web_access\":false"), json)
    }
}
