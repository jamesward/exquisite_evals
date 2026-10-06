# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | mcp | 6/8 (budget) | FAIL 0.00 | FAIL 0.22 | 457550 | 1077 | 0 | 13 | 12 | 69.4 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 0/1 | 0/1 | 0/1 | 458627 | 69.4 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 958 | 185 | 2.0 | 0 |
| jev | 0/1 | 1975 | 39 | 0.7 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## spring-ai-hostedtool / mcp

```
Based on my investigation, I need to clarify an important finding:

## What I Could Verify

**The `HostedTool` sealed interface does not exist in Spring AI's OpenAI module** (version 2.0.1, the latest as of 2026-10-06).

Based on my search through:
- The Spring AI OpenAI module (`org.springframework.ai:spring-ai-openai:2.0.1`) 
- The underlying OpenAI Java SDK (`com.openai:openai-java:4.76.0`)

I found no `HostedTool` sealed interface in either library.

## What I Found Instead

The OpenAI Java SDK (which Spring AI OpenAI uses internally) has a **`Tool`** class that implements a union type pattern with methods like:
- `function()` - for function tools
- `fileSearch()` - for file search tools  
- `codeInterpreter()` - for code interpreter
- `webSearch()` - for web search
- `mcp()` - for MCP tools
- etc.

There's also a **`_json()`** method on the `Tool` class that returns `Optional<JsonValue>` which allows access to raw JSON, but this is not a separate "raw JSON tool type" - it's just an accessor for the underlying JSON representation.

## What I Could Not Verify

I could not verify:
1. The existence of a `HostedTool` sealed interface in any version of Spring AI's OpenAI module
2. Any type specifically designed for sending "raw JSON tool definitions"

If you're working with Spring AI 2.0.1 and need to send raw tool definitions, you would typically use the OpenAI Java SDK's `Tool` class directly or work with the `JsonValue` type from the SDK's core package.
```

- checks: facts 6/8 missing=[version 2.1.0-M1, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claim that HostedTool sealed interface does not exist in any version of Spring AI's OpenAI module, Claim that HostedTool does not exist in version 2.0.1 (contradicts reference), Claim that the OpenAI Java SDK's Tool class has methods like function(), fileSearch(), etc. and a _json() method returning Optional<JsonValue> (these are not mentioned in the reference and may be incorrect or irrelevant)], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant's answer is factually inconsistent with the reference, which confirms HostedTool exists in the 2.1.0-M1 milestone, and it fails to provide any of the requested information about its permitted types or the Raw type.
- jev: FAIL 0.22 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.14, completeness.value=0.29, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.14, needs at least 0.60)
- completeness: rated "Some of it" (0.88), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, ImageGeneration]
