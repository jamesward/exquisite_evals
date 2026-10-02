# Eval results

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | mcp-toolsearch-vector | 7/7 | FAIL 0.00 | FAIL 0.49 | 82011 | 2874 | 1182 | 19 | 17 | 38.5 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp-toolsearch-vector | 1/1 | 0/1 | 0/1 | 86067 | 38.5 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 667 | 694 | 3.8 | 0 |
| jev | 0/1 | 1910 | 39 | 0.4 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 0/1 | jev-semantic vs code checks 0/1

## spring-ai-hostedtool / mcp-toolsearch-vector

```
The **stable‑release** of Spring AI’s OpenAI module ( `org.springframework.ai:spring‑ai‑openai:2.0.1` ) does **not contain** the sealed interface `HostedTool`. The `HostedTool` hierarchy (with variants such as `WebSearch`, `ImageGeneration`, `FileSearch`, `CodeInterpreter`, `Mcp`, and `Raw`) only appears in the **milestone** version `2.1.0‑M1`.

Therefore, in the latest **release** there are no permitted types for `HostedTool`, and consequently there is no type that lets you send a tool definition as raw JSON. To use those `HostedTool` variants—including the `Raw` type (which is the one that accepts a raw JSON definition—you would need to depend on the **milestone** version `2.1.0‑M1` or a later release that incorporates it.
```

- checks: facts 7/7
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[claims that the latest release (stable 2.0.1) does not contain HostedTool and thus has no permitted types, contradicting the reference which states the latest release is 2.1.0-M1 and includes six HostedTool records, fails to list the six permitted types and the Raw type that allows raw JSON], trap_handled=null, semantic_pass=false, code_checks_pass=true} The answer incorrectly identifies the latest release and omits the six HostedTool variants, misrepresenting the API information from the reference.
- jev: FAIL 0.49 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.36, completeness.value=0.62, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.36, needs at least 0.70)
