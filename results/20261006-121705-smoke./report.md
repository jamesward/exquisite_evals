# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp | 15/15 | PASS 1.00 | PASS 0.85 | 26167 | 671 | 0 | 6 | 7 | 18.5 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 1/1 | 1/1 | 1/1 | 26838 | 18.5 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 1/1 | 833 | 76 | 2.5 | 0 |
| jev | 1/1 | 1573 | 39 | 0.5 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## jevjudge-gav / mcp

```
**Maven coordinates:**
```
org.springaicommunity:typesafe-spring-ai:0.4.0
```

**JevJudge.Builder methods** (all return `JevJudge.Builder` except `build()`):

| Method | Parameters |
|--------|------------|
| `noul` | `(String name, Noul noul, double minimum)` |
| `score` | `(String name, Score score, double minimum)` |
| `choice` | `(String name, Choice choice, String... acceptedOptions)` |
| `check` | `(String name, Predicate<JevJudgeInput> check, String defect)` |
| `criterion` | `(JevCriterion criterion)` |
| `minConfidence` | `(double minConfidence)` |
| `failOnInconclusive` | `(boolean failOnInconclusive)` |
| `failOnError` | `(boolean failOnError)` |
| `failFast` | `(boolean failFast)` |
| `feedbackRenderer` | `(Function<List<JevFinding>, String> feedbackRenderer)` |
| `build` | `()` → returns `JevJudge` |

**Default value of `minConfidence`:**  
`0.6` (60%)

As documented: *"The default `minConfidence`: a clear majority, 60%, of a score's or choice's probability has to support the verdict before it is acted on."*
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer matches the reference exactly in coordinates, methods, parameter types, and default value, with no incorrect or unsupported claims.
- jev: PASS 0.85 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.79, completeness.value=0.90, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]
