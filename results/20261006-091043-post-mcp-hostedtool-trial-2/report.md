# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | mcp | 8/8 | PASS 1.00 | FAIL 0.75 | 215717 | 1349 | 0 | 24 | 23 | 71.7 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 1/1 | 1/1 | 0/1 | 217066 | 71.7 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 1/1 | 908 | 92 | 1.3 | 0 |
| jev | 0/1 | 2466 | 39 | 0.6 | 0 |

Agreement: llm vs jev 0/1 | semantic-only llm vs jev 0/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 0/1

## spring-ai-hostedtool / mcp

```
I found the `HostedTool` sealed interface in Spring AI OpenAI version **2.1.0-M1**. Let me provide the answer based on the source code I've read.

## Answer

In **Spring AI OpenAI 2.1.0-M1** (latest milestone), the sealed interface `HostedTool` permits the following six types:

| Type | Description |
|------|-------------|
| `HostedTool.WebSearch` | Search the public web |
| `HostedTool.FileSearch` | Search vector stores hosted at OpenAI |
| `HostedTool.CodeInterpreter` | Run Python in a sandboxed container |
| `HostedTool.Mcp` | Call tools on a remote MCP server |
| `HostedTool.ImageGeneration` | Generate images |
| `HostedTool.Raw` | **Send a tool definition as raw JSON** |

The `Raw` type is the **escape hatch** that lets you send a tool definition as raw JSON. As the Javadoc states:

> "A tool declared as the raw request JSON, for tools this class does not type yet."

You'll use it like this:
```java
HostedTool rawTool = new HostedTool.Raw(Map.of("type", "local_shell", /* ... other fields */));
```

**Note:** The `HostedTool` interface is in the new `responses` API (`org.springframework.ai.openai.responses` package) introduced in version 2.1.0-M1 and is not present in the stable 2.0.x releases.
```

- checks: facts 8/8
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant correctly identifies HostedTool exists only in 2.1.0-M1, lists all six permitted types, and accurately identifies Raw as the type for sending raw JSON, while noting the version context appropriately.
- jev: FAIL 0.75 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.59, completeness.value=0.90, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.59, needs at least 0.60)
