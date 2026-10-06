# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp | 15/15 | PASS 1.00 | PASS 0.80 | 28995 | 604 | 0 | 6 | 6 | 23.4 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 1/1 | 1/1 | 1/1 | 29599 | 23.4 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 1/1 | 889 | 85 | 1.9 | 0 |
| jev | 1/1 | 1551 | 39 | 0.6 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## jevjudge-gav / mcp

```
**Maven coordinates (latest version):**
```
org.springaicommunity:typesafe-spring-ai:0.4.0
```

**JevJudge.Builder methods with parameter types:**

| Method | Parameter Types | Return Type |
|--------|-----------------|-------------|
| `noul` | `String name, Noul noul, double minimum` | `JevJudge.Builder` |
| `score` | `String name, Score score, double minimum` | `JevJudge.Builder` |
| `choice` | `String name, Choice choice, String... acceptedOptions` | `JevJudge.Builder` |
| `check` | `String name, Predicate<JevJudgeInput> check, String defect` | `JevJudge.Builder` |
| `criterion` | `JevCriterion criterion` | `JevJudge.Builder` |
| `minConfidence` | `double minConfidence` | `JevJudge.Builder` |
| `failOnInconclusive` | `boolean failOnInconclusive` | `JevJudge.Builder` |
| `failOnError` | `boolean failOnError` | `JevJudge.Builder` |
| `failFast` | `boolean failFast` | `JevJudge.Builder` |
| `feedbackRenderer` | `Function<List<JevFinding>, String> feedbackRenderer` | `JevJudge.Builder` |
| `build` | (none) | `JevJudge` |

**Default value of `minConfidence`:** `0.6` (60%) — defined by the constant `JevJudge.DEFAULT_MIN_CONFIDENCE`. This represents a clear majority; a coin flip between pass and fail would support it at 50%.
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly provides all requested information—Maven coordinates, Builder methods with parameters, and the default minConfidence value—matching the reference exactly without any incorrect claims.
- jev: PASS 0.80 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.67, completeness.value=0.94, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]
