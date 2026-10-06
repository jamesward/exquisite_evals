# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp | 0/15 (run error) | ERR | FAIL 0.47 | 0 | 0 | 0 | 0 | 0 | 1.3 |
| jevjudge-gav | mcp-toolsearch-jev | 0/15 (run error) | ERR | FAIL 0.47 | 0 | 0 | 0 | 0 | 0 | 0.4 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 0/1 | 0/1 | 0/1 | 0 | 1.3 |
| mcp-toolsearch-jev | 0/1 | 0/1 | 0/1 | 0 | 0.4 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/2 | 0 | 0 | 0.4 | 2 |
| jev | 0/2 | 1548 | 78 | 0.8 | 0 |

Agreement: llm vs jev - | semantic-only llm vs jev - | llm-semantic vs code checks - | jev-semantic vs code checks 2/2

## jevjudge-gav / mcp

```
UnauthorizedException: 401: The security token included in the request is expired
```

- checks: facts 0/15 missing=[groupId, artifactId, version 0.4.0, default minConfidence 0.6, noul, score, choice, check, criterion, minConfidence, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.47 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.93, completeness.value=0.00, semantic_pass=false} - completeness: rated "None of it" (0.00), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [groupId, artifactId, version 0.4.0, default minConfidence 0.6, noul, score, choice, check, criterion, minConfidence, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]

## jevjudge-gav / mcp-toolsearch-jev

```
UnauthorizedException: 401: The security token included in the request is expired
```

- checks: facts 0/15 missing=[groupId, artifactId, version 0.4.0, default minConfidence 0.6, noul, score, choice, check, criterion, minConfidence, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.47 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.93, completeness.value=0.00, semantic_pass=false} - completeness: rated "None of it" (0.00), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [groupId, artifactId, version 0.4.0, default minConfidence 0.6, noul, score, choice, check, criterion, minConfidence, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]
