# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | mcp | 2/8 | FAIL 0.08 | FAIL 0.33 | 239889 | 1905 | 0 | 27 | 25 | 84.0 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 0/1 | 0/1 | 0/1 | 241794 | 84.0 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 927 | 230 | 5.6 | 0 |
| jev | 0/1 | 2530 | 39 | 0.4 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## spring-ai-hostedtool / mcp

```
Based on my investigation, I need to clarify something important: **the `HostedTool` sealed interface does not exist in the current latest release (2.0.1) or the available sources of Spring AI's OpenAI module.**

Here's what I found:

1. **Spring AI OpenAI module latest release**: `2.0.1` (as of 2026-10-06)

2. **No `HostedTool` interface exists** in:
   - `spring-ai-openai:2.0.1` (current latest release)
   - `spring-ai-model:2.0.1`
   - The Javadoc only shows 4 packages: `org.springframework.ai.openai`, `org.springframework.ai.openai.http.okhttp`, `org.springframework.ai.openai.metadata`, and `org.springframework.ai.openai.setup` — none contain a `HostedTool` type

3. There is a newer **milestone version 2.1.0-M1** available, which I could not fully explore due to tool limits. This might contain the `HostedTool` interface you're asking about.

**Suggestion**: The `HostedTool` interface you're asking about may be:
- In the upcoming 2.1.0 milestone release (2.1.0-M1 was detected)
- Part of a different module or API not yet publicly documented
- Perhaps confused with the **OpenAI Java SDK** (`com.openai:openai-java`) which contains `HostedToolPermission` types

If you can confirm the exact version or provide more context about where you encountered this interface, I can investigate further.
```

- checks: facts 2/8 missing=[WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
- llm: FAIL 0.08 {factually_consistent=false, completeness=1, hallucinations=["No `HostedTool` interface exists" in 2.0.1 (this is correct) but also states it does not exist "in the available sources" or "at all," which contradicts the reference that it exists in 2.1.0-M1., Claims the Javadoc only shows 4 packages, implying a comprehensive check that omits the `org.springframework.ai.openai.responses` package where HostedTool resides in 2.1.0-M1., Suggests HostedTool might be confused with OpenAI Java SDK's `HostedToolPermission`, which is an unsupported claim.], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant correctly states HostedTool does not exist in 2.0.1 but incorrectly denies its existence in any available source and invents unrelated SDK types, contradicting the reference; it partially addresses version ambiguity but fails to list the permitted types or identify Raw.
- jev: FAIL 0.33 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.35, completeness.value=0.32, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.35, needs at least 0.60)
- completeness: rated "Some of it" (0.95), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
