# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | mcp | 1/8 (budget) | FAIL 0.00 | FAIL 0.34 | 310970 | 1332 | 0 | 18 | 17 | 94.8 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 0/1 | 0/1 | 0/1 | 312302 | 94.8 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 981 | 203 | 6.2 | 0 |
| jev | 0/1 | 2210 | 39 | 0.3 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## spring-ai-hostedtool / mcp

```
Based on my search, I could **not** find a `HostedTool` sealed interface in the Spring AI OpenAI module version `2.0.1` or the OpenAI Java SDK version `4.76.0`. 

Here's what I did find and could verify:

## What I Verified

**Spring AI OpenAI Module (2.0.1):**
- Uses the OpenAI Java SDK (`com.openai:openai-java`) as its underlying client
- The `OpenAiChatOptions` class allows configuring tools via `toolCallbacks` (List of `ToolCallback`) and a `toolChoice` Object field

**OpenAI Java SDK (4.76.0):**
- Contains `HostedToolPermission` classes in the admin/organization/projects packages (for managing tool permissions)
- Contains `ChatCompletionTool`, `ChatCompletionFunctionTool`, and `ChatCompletionCustomTool` for defining tools in chat completions
- Contains `ChatCompletionAllowedTools` for specifying which tools are allowed

## What I Could NOT Verify

The specific **`HostedTool`** sealed interface you asked about:
- **Does not appear** in the current Spring AI OpenAI module `2.0.1`
- **Does not appear** in the OpenAI Java SDK `4.76.0`

This could mean:
1. `HostedTool` was added in a **newer version** of Spring AI or the OpenAI SDK after what I was able to query
2. It may be in a **different artifact** (such as `spring-ai-model` or a specific Spring AI starter)
3. It may be an **internal/private API** not exposed in the public classes I searched

If you're working with a pre-release or milestone version of Spring AI (e.g., `1.0.0-M6` or newer), the API may have changed since the versions I was able to query.
```

- checks: facts 1/8 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claiming HostedTool does not exist in Spring AI OpenAI module 2.0.1 or OpenAI Java SDK 4.76.0 (it exists in Spring AI 2.1.0-M1), Mentioning OpenAI Java SDK version 4.76.0 which is irrelevant to the question, Incorrectly suggesting HostedTool might be in spring-ai-model or a starter, Mentioning 1.0.0-M6 as a possible newer version when 2.1.0-M1 is the relevant milestone], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant failed to identify that HostedTool exists in Spring AI 2.1.0-M1, missed all six permitted types, and didn't identify Raw as the JSON tool definition type.
- jev: FAIL 0.34 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.38, completeness.value=0.30, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.38, needs at least 0.60)
- completeness: rated "Some of it" (0.90), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
