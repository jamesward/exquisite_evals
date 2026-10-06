# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | web-brave | 14/15 | FAIL 0.17 | FAIL 0.29 | 146436 | 1049 | 12434 | 17 | 16 | 43.0 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| web-brave | 0/1 | 0/1 | 0/1 | 159919 | 43.0 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/1 | 833 | 188 | 2.3 | 0 |
| jev | 0/1 | 2065 | 39 | 0.4 | 0 |

Agreement: llm vs jev 1/1 | semantic-only llm vs jev 1/1 | llm-semantic vs code checks 1/1 | jev-semantic vs code checks 1/1

## jevjudge-gav / web-brave

```
I found that version 0.3.0 is the latest released version (not SNAPSHOT). The documentation site (spring-ai-community.github.io) showed 0.2.0 as a release version, and the Spring blog post on October 2, 2026 confirms 0.3.0 is released. Let me verify this is the latest by checking the release on GitHub:

Now I have all the information I need. Based on my research:

**Maven Coordinates (latest version):**
`org.springaicommunity:typesafe-spring-ai:0.3.0`

**JevJudge.Builder methods with parameter types:**
- `noul(String id, Noul expected, double weight) Builder`
- `score(String id, Score expected, double weight) Builder`
- `choice(String id, Choice expected, String... options) Builder`
- `check(String id, Predicate<JevJudgeInput> predicate, String description) Builder`
- `criterion(JevCriterion criterion) Builder`
- `minConfidence(double minConfidence) Builder`
- `failOnError(boolean failOnError) Builder`
- `failFast(boolean failFast) Builder`
- `failOnInconclusive(boolean failOnInconclusive) Builder`
- `feedbackRenderer(Function<List<JevFinding>, String> feedbackRenderer) Builder`
- `build() JevJudge`

**Default value of minConfidence:** `0.6`
```

- checks: facts 14/15 missing=[version 0.4.0]
- llm: FAIL 0.17 {factually_consistent=false, completeness=2, hallucinations=[Incorrect version (0.3.0 vs 0.4.0), Incorrect method parameter names: 'noul' uses 'id, expected, weight' vs 'name, noul, minimum'; 'score' uses 'id, expected, weight' vs 'name, score, minimum'; 'choice' uses 'id, expected, options' vs 'name, choice, acceptedOptions'; 'check' uses 'id, predicate, description' vs 'name, check, defect'], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant's answer contains multiple factual errors in the version and method parameter names, making it factually inconsistent, and it correctly identifies only the default minConfidence and the general set of method names.
- jev: FAIL 0.29 {grounded=FAILED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.02, completeness.value=0.56, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.02, needs at least 0.60)
- required_facts: missing required facts: [version 0.4.0]
