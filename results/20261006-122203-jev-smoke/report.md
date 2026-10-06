# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp-toolsearch-jev | 15/15 | PASS 1.00 | PASS 0.80 | 29592 | 1481 | 0 | 9 | 9 | 40.4 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp-toolsearch-jev | 1/1 | 1/1 | 1/1 | 31073 | 40.4 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 1/1 | 1375 | 99 | 3.0 | 0 |
| jev | 1/1 | 2245 | 39 | 0.4 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## jevjudge-gav / mcp-toolsearch-jev

```
Now I have all the information needed. The `JevJudge` class is in the maven artifact `org.springaicommunity:typesafe-spring-ai:0.4.0`, and the static constant `DEFAULT_MIN_CONFIDENCE` is documented as 0.6 (60%).

  Let me summarize the findings:

  **Maven Coordinates:**
  - groupId: `org.springaicommunity`
  - artifactId: `typesafe-spring-ai`
  - version: `0.4.0` (latest release)

  **JevJudge.Builder Methods with parameter types:**
  1. `build()` - no parameters
  2. `check(String name, Predicate<JevJudgeInput> check, String defect)` - String, Predicate<JevJudgeInput>, String
  3. `choice(String name, Choice choice, String... acceptedOptions)` - String, Choice, String[]
  4. `criterion(JevCriterion criterion)` - JevCriterion
  5. `failFast(boolean failFast)` - boolean
  6. `failOnError(boolean failOnError)` - boolean
  7. `failOnInconclusive(boolean failOnInconclusive)` - boolean
  8. `feedbackRenderer(Function<List<JevFinding>, String> feedbackRenderer)` - Function<List<JevFinding>, String>
  9. `minConfidence(double minConfidence)` - double
  10. `noul(String name, Noul noul, double minimum)` - String, Noul, double
  11. `score(String name, Score score, double minimum)` - String, Score, double

  **Default value of minConfidence:**
  The constant `JevJudge.DEFAULT_MIN_CONFIDENCE` is 0.6 (60%).

  Actually, looking at the documentation more carefully: "a clear majority, 60%, of a score's or choice's probability has to support the verdict before it is acted on." Let me verify the exact value by looking at constant field values if available. But based on the text, I can confirm that `DEFAULT_MIN_CONFIDENCE = 0.6`.

  Let me provide the final answer concisely. </think> **Maven Coordinates:**  
`org.springaicommunity:typesafe-spring-ai:0.4.0`

**JevJudge.Builder Methods (with parameter types):**

| Return Type | Method | Parameters |
|-------------|--------|------------|
| `JevJudge.Builder` | `check` | `String name, Predicate<JevJudgeInput> check, String defect` |
| `JevJudge.Builder` | `choice` | `String name, Choice choice, String... acceptedOptions` |
| `JevJudge.Builder` | `criterion` | `JevCriterion criterion` |
| `JevJudge.Builder` | `failFast` | `boolean failFast` |
| `JevJudge.Builder` | `failOnError` | `boolean failOnError` |
| `JevJudge.Builder` | `failOnInconclusive` | `boolean failOnInconclusive` |
| `JevJudge.Builder` | `feedbackRenderer` | `Function<List<JevFinding>, String> feedbackRenderer` |
| `JevJudge.Builder` | `minConfidence` | `double minConfidence` |
| `JevJudge.Builder` | `noul` | `String name, Noul noul, double minimum` |
| `JevJudge.Builder` | `score` | `String name, Score score, double minimum` |
| `JevJudge` | `build` | `()` (no parameters) |

**Default value of `minConfidence`:**  
`0.6` (60%). This is defined by the constant `JevJudge.DEFAULT_MIN_CONFIDENCE`, which represents "a clear majority" of a score's or choice's probability required to support a verdict before it is acted on.
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly provides all requested information: Maven coordinates match exactly, all Builder methods with parameter types are listed (though in different order), and the default minConfidence value of 0.6 is accurately stated with the correct constant name.
- jev: PASS 0.80 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.73, completeness.value=0.88, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]
