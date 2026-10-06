# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | mcp | 7/8 | FAIL 1.00 | FAIL 0.49 | 23414 | 564 | 0 | 6 | 5 | 13.9 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 0/1 | 0/1 | 0/1 | 23978 | 13.9 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 809 | 88 | 2.6 | 0 |
| jev | 0/1 | 1396 | 39 | 0.4 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 0/1 | llm-semantic vs code checks 0/1 | jev-semantic vs code checks 1/1

## spring-ai-hostedtool / mcp

```
In Spring AI OpenAI **2.1.0-M1** (the latest release), the sealed interface `HostedTool` permits these 6 types:

1. **`HostedTool.WebSearch`** — Search the public web
2. **`HostedTool.FileSearch`** — Search vector stores hosted at OpenAI
3. **`HostedTool.CodeInterpreter`** — Run Python in a sandboxed container
4. **`HostedTool.Mcp`** — Call tools on a remote MCP server from inside OpenAI's request
5. **`HostedTool.ImageGeneration`** — Generate images
6. **`HostedTool.Raw`** — A tool declared as the raw request JSON

**The `HostedTool.Raw` record** is the one that lets you send a tool definition as raw JSON. It's described as an "escape hatch for tools OpenAI ships before Spring AI types them." It holds a `Map<String, Object>` representing the tool object (e.g., `Map.of("type", "local_shell")`).
```

- checks: facts 7/8 missing=[says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it]
- llm: FAIL 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=false} The assistant correctly identifies the 2.1.0-M1 milestone as the relevant version, lists all six permitted types, and accurately specifies HostedTool.Raw as the one for raw JSON, matching the reference.
- jev: FAIL 0.49 {grounded=FAILED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.29, completeness.value=0.69, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.29, needs at least 0.60)
- required_facts: missing required facts: [says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it]
