package com.example.demo

import io.modelcontextprotocol.client.McpSyncClient
import org.springaicommunity.typesafe.TypeSafeClient
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

/**
 * Eval demo: `./gradlew bootRun` runs every task on every enabled arm and judges each answer with both the
 * LLM judge and Jev. Narrow with `-PevalTasks=jevjudge-gav,jackson3-ptv -PevalArms=mcp,mcp-toolsearch`.
 */
@SpringBootApplication
@EnableConfigurationProperties(EvalProperties::class)
class EvalApp {

    @Bean
    fun armCatalog(props: EvalProperties, builder: ChatClient.Builder, mcpClients: List<McpSyncClient>) =
        // the MCP starter registers the clients as one List bean, so inject the list (not ObjectProvider<McpSyncClient>)
        ArmCatalog(props, builder, {
            SyncMcpToolCallbackProvider.builder().mcpClients(mcpClients).build().toolCallbacks.toList()
        })

    /**
     * Both judges always run, so their verdicts, cost and agreement can be compared. The LLM judge gets its own
     * Chat Completions model (`evals.judge.model`), from a different family than the agent, so it isn't grading
     * its own answers.
     */
    @Bean
    fun judges(
        typeSafeClient: TypeSafeClient,
        @Value("\${evals.judge.model}") judgeModel: String,
        @Value("\${spring.ai.openai.base-url}") baseUrl: String,
        @Value("\${spring.ai.openai.api-key}") apiKey: String,
    ): List<EvalJudge> {
        val judgeChatModel = OpenAiChatModel.builder().options(
            OpenAiChatOptions.builder().baseUrl(baseUrl).apiKey(apiKey).model(judgeModel).temperature(0.0).build(),
        ).build()
        return listOf(LlmJudge(ChatClient.builder(judgeChatModel)), JevAsJudge(typeSafeClient))
    }

    @Bean
    @ConditionalOnProperty("evals.run", havingValue = "true")
    fun evalCommand(
        catalog: ArmCatalog,
        judges: List<EvalJudge>,
        @Value("\${evals.tasks:all}") tasks: List<String>,
        @Value("\${evals.arms:all}") arms: List<String>,
        @Value("\${evals.budget.max-tool-calls:25}") maxToolCalls: Int,
        @Value("\${evals.budget.max-model-calls:30}") maxModelCalls: Int,
        @Value("\${evals.budget.max-input-tokens:250000}") maxInputTokens: Int,
        @Value("\${evals.label:}") label: String,
        @Value("\${spring.ai.openai.chat.api}") agentApi: String,
        @Value("\${spring.ai.openai.chat.model}") agentModel: String,
        @Value("\${evals.judge.model}") judgeModel: String,
    ) = CommandLineRunner {
        val selectedArms = if (arms.any { it.equals("all", ignoreCase = true) }) catalog.arms else arms.map(catalog::arm)
        EvalRunner(catalog, judges, RunBudget(maxToolCalls, maxModelCalls, maxInputTokens), label = label,
            models = RunModels(agentModel, agentApi, judgeModel)).run(TaskCatalog.select(tasks), selectedArms)
    }
}

fun main(args: Array<String>) {
    runApplication<EvalApp>(*args)
}
