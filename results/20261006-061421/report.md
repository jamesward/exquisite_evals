# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | web-brave | 14/15 | FAIL 0.25 | FAIL 0.66 | 48085 | 600 | 25826 | 7 | 6 | 40.5 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| web-brave | 0/1 | 0/1 | 0/1 | 74511 | 40.5 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 820 | 112 | 3.6 | 0 |
| jev | 0/1 | 1530 | 39 | 0.7 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 0/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 0/1

## jevjudge-gav / web-brave

```
Based on the documentation, here is the information about `JevJudge`:

## Maven Coordinates
**`org.springaicommunity:typesafe-spring-ai:0.4.0`** (latest version)

## JevJudge.Builder Methods

| Method | Parameter Types |
|--------|-----------------|
| `noul` | `(String, Noul, double)` |
| `score` | `(String, Score, double)` |
| `choice` | `(String, Choice, String...)` |
| `check` | `(String, Predicate<JevJudgeInput>, String)` |
| `criterion` | `(JevCriterion)` |
| `minConfidence` | `(double)` |
| `failOnError` | `(boolean)` |
| `failFast` | `(boolean)` |
| `failOnInconclusive` | `(boolean)` |
| `build` | `()` |

## Default Value of minConfidence
**`0.6`** (from the constant `DEFAULT_MIN_CONFIDENCE`)

Sources:
- [Spring AI Community Spring AI TypeSafe - Quick Start](https://github.com/spring-ai-community/spring-ai-typesafe)
- [JevJudge Documentation](https://spring-ai-community.github.io/spring-ai-typesafe/latest-snapshot/judge/JevJudge/)
```

- checks: facts 14/15 missing=[feedbackRenderer]
- llm: FAIL 0.25 {factually_consistent=false, completeness=3, hallucinations=[Missing method: feedbackRenderer(Function<List<JevFinding>, String> feedbackRenderer), Incorrect method order: failOnInconclusive should be before failOnError], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer contains correct coordinates and default value but omits one method and misorders others, making it factually inconsistent despite covering most requirements.
- jev: FAIL 0.66 {grounded=PASSED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.67, completeness.value=0.66, semantic_pass=true} - required_facts: missing required facts: [feedbackRenderer]
