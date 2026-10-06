# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp | 15/15 | PASS 1.00 | PASS 0.91 | 25179 | 667 | 0 | 6 | 6 | 18.3 |
| jevjudge-gav | mcp-toolsearch-jev | 15/15 | PASS 1.00 | PASS 0.89 | 26294 | 908 | 0 | 12 | 7 | 19.7 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 1/1 | 1/1 | 1/1 | 25846 | 18.3 |
| mcp-toolsearch-jev | 1/1 | 1/1 | 1/1 | 27202 | 19.7 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 2/2 | 1711 | 176 | 4.5 | 0 |
| jev | 2/2 | 3159 | 78 | 1.1 | 0 |

Agreement: llm vs jev 2/2 | semantic-only llm vs jev 2/2 | llm-semantic vs code checks 2/2 | jev-semantic vs code checks 2/2

## jevjudge-gav / mcp

```
Now I have all the information I need.

## Maven Coordinates
**`org.springaicommunity:typesafe-spring-ai:0.4.0`**

---

## `JevJudge.Builder` Methods with Parameter Types

| Method | Parameter Types | Return Type |
|--------|----------------|-------------|
| `noul` | `(String name, Noul noul, double minimum)` | `JevJudge.Builder` |
| `score` | `(String name, Score score, double minimum)` | `JevJudge.Builder` |
| `choice` | `(String name, Choice choice, String... acceptedOptions)` | `JevJudge.Builder` |
| `check` | `(String name, Predicate<JevJudgeInput> check, String defect)` | `JevJudge.Builder` |
| `criterion` | `(JevCriterion criterion)` | `JevJudge.Builder` |
| `minConfidence` | `(double minConfidence)` | `JevJudge.Builder` |
| `failOnInconclusive` | `(boolean failOnInconclusive)` | `JevJudge.Builder` |
| `failOnError` | `(boolean failOnError)` | `JevJudge.Builder` |
| `failFast` | `(boolean failFast)` | `JevJudge.Builder` |
| `feedbackRenderer` | `(Function<List<JevFinding>, String> feedbackRenderer)` | `JevJudge.Builder` |
| `build` | `()` | `JevJudge` |

---

## Default Value of `minConfidence`

**`0.6`** (i.e., 60% — a clear majority threshold)
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly provides all requested information—Maven coordinates, complete Builder method list with parameters, and the default minConfidence value—matching the reference exactly without any incorrect claims.
- jev: PASS 0.91 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.88, completeness.value=0.94, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jevjudge-gav / mcp-toolsearch-jev

```
Based on the Javadoc retrieved, I can now provide a comprehensive answer.

## Maven Coordinates

```
org.springaicommunity:typesafe-spring-ai:0.4.0
```

## JevJudge.Builder Methods (with parameter types)

1. **`check(String name, Predicate<JevJudgeInput> check, String defect)`**
2. **`choice(String name, Choice choice, String... acceptedOptions)`**
3. **`criterion(JevCriterion criterion)`**
4. **`failFast(boolean failFast)`**
5. **`failOnError(boolean failOnError)`**
6. **`failOnInconclusive(boolean failOnInconclusive)`**
7. **`feedbackRenderer(Function<List<JevFinding>, String> feedbackRenderer)`**
8. **`minConfidence(double minConfidence)`**
9. **`noul(String name, Noul noul, double minimum)`**
10. **`score(String name, Score score, double minimum)`**
11. **`build()`** — returns `JevJudge`

## Default Value of minConfidence

The default value of `minConfidence` is **0.6** (60%), as defined by the constant `JevJudge.DEFAULT_MIN_CONFIDENCE`. According to the documentation, this means "a clear majority, 60%, of a score's or choice's probability has to support the verdict before it is acted on."
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly provides all requested information—Maven coordinates, all Builder methods with parameter types, and the default minConfidence value—matching the reference exactly without any incorrect or invented claims.
- jev: PASS 0.89 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.87, completeness.value=0.90, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]
