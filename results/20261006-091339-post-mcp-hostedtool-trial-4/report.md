# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | mcp | 2/8 (budget) | FAIL 0.00 | FAIL 0.33 | 333504 | 1474 | 0 | 20 | 19 | 69.3 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 0/1 | 0/1 | 0/1 | 334978 | 69.3 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 972 | 124 | 1.5 | 0 |
| jev | 0/1 | 2345 | 39 | 0.4 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## spring-ai-hostedtool / mcp

```
Based on my investigation, I need to clarify an important finding:

## What I Found

I could not locate a `HostedTool` sealed interface in Spring AI's OpenAI module (`org.springframework.ai:spring-ai-openai:2.0.1`). This type does not appear to exist in the current codebase.

### What I Did Find

In the **OpenAI Java SDK** (`com.openai:openai-java:4.76.0`), which Spring AI depends on, there is a **`ChatCompletionTool`** class (not a sealed interface). It uses a visitor pattern and has two variants:
- **`ChatCompletionFunctionTool`** – via `function()` / `asFunction()`
- **`ChatCompletionCustomTool`** – via `custom()` / `asCustom()`

The `ChatCompletionCustomTool` appears to be the type that allows sending a custom tool definition, though this is not a "raw JSON" type per se.

## What I Could Not Verify

- **The exact name `HostedTool`** – This sealed interface does not appear to exist in Spring AI's OpenAI module as of version 2.0.1.
- **A type for sending raw JSON tool definitions** – I could not find a specific "raw JSON" variant.

## Possible Explanations

1. The `HostedTool` sealed interface may be in a **different module** of Spring AI (not the OpenAI-specific module)
2. It may have been **added after version 2.0.1** and not yet available
3. It may be an **internal implementation detail** not exposed in public APIs
4. The user may be referring to a **different library** or a **custom abstraction** in their codebase

If you can provide more context about where you encountered this type, I can help locate it more precisely.
```

- checks: facts 2/8 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[ChatCompletionTool class in OpenAI Java SDK, ChatCompletionFunctionTool and ChatCompletionCustomTool variants, ChatCompletionCustomTool as the type for custom tool definitions], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant failed to locate HostedTool despite its existence in Spring AI 2.1.0-M1, instead inventing unrelated OpenAI SDK classes, making it factually inconsistent and scoring zero for completeness.
- jev: FAIL 0.33 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.39, completeness.value=0.27, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.39, needs at least 0.60)
- completeness: rated "Some of it" (0.82), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
