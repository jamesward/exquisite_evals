# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp-toolsearch | 1/15 | FAIL 0.00 | FAIL 0.05 | 21105 | 778 | 0 | 12 | 8 | 17.9 |
| jevjudge-gav | mcp-toolsearch-vector | 15/15 | PASS 1.00 | PASS 0.89 | 22298 | 686 | 1187 | 7 | 6 | 18.1 |
| jevjudge-gav | mcp-toolsearch-jev | 15/15 | PASS 1.00 | PASS 0.80 | 32331 | 1029 | 0 | 12 | 9 | 24.1 |
| jackson3-ptv | mcp-toolsearch | 9/10 | FAIL 1.00 | FAIL 0.86 | 27636 | 865 | 0 | 7 | 7 | 17.2 |
| jackson3-ptv | mcp-toolsearch-vector | 10/10 | PASS 1.00 | PASS 0.85 | 26400 | 1085 | 1199 | 6 | 7 | 32.0 |
| jackson3-ptv | mcp-toolsearch-jev | 10/10 | PASS 1.00 | FAIL 0.69 | 26341 | 1180 | 0 | 6 | 7 | 31.0 |
| spring-ai-hostedtool | mcp-toolsearch | 0/8 (run error) | ERR | FAIL 0.40 | 74774 | 513 | 0 | 7 | 5 | 27.1 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 0/8 (run error) | ERR | FAIL 0.45 | 0 | 0 | 0 | 0 | 0 | 2.4 |
| spring-ai-hostedtool | mcp-toolsearch-jev | 0/8 (run error) | ERR | FAIL 0.45 | 0 | 0 | 0 | 0 | 0 | 0.5 |
| agentutils-shell-trap | mcp-toolsearch | 0/5 (run error) | ERR | FAIL 0.33 | 0 | 0 | 0 | 0 | 0 | 0.4 |
| agentutils-shell-trap | mcp-toolsearch-vector | 0/5 (run error) | ERR | FAIL 0.33 | 0 | 0 | 0 | 0 | 0 | 1.5 |
| agentutils-shell-trap | mcp-toolsearch-jev | 0/5 (run error) | ERR | FAIL 0.33 | 0 | 0 | 0 | 0 | 0 | 0.6 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp-toolsearch | 0/4 | 0/4 | 0/4 | 125671 | 15.7 |
| mcp-toolsearch-vector | 2/4 | 2/4 | 2/4 | 52855 | 13.5 |
| mcp-toolsearch-jev | 2/4 | 2/4 | 1/4 | 60881 | 14.1 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 4/12 | 6680 | 572 | 21.2 | 6 |
| jev | 3/12 | 16329 | 525 | 4.6 | 0 |

Agreement: llm vs jev 5/6 | semantic-only llm vs jev 5/6 | llm-semantic vs code checks 5/6 | jev-semantic vs code checks 10/12

## jevjudge-gav / mcp-toolsearch

```
Based on my examination of the source code, I have the information you need. However, I need to clarify something important: **The `JevJudge` class in this library does not have a traditional Builder pattern with a separate `Builder` class**. Instead, it uses factory methods with default parameters.

## Maven Coordinates

**`com.jamesward:zio-evals_3:0.2.0`**

This is a Scala 3 library (artifactId ends with `_3`).

## Builder-like Methods

The `JevJudge` object provides two factory methods for construction:

### 1. `apply(client: TypeSafeAI.Client, passThreshold: Double = 0.5): JevJudge`
- **Parameters:**
  - `client: TypeSafeAI.Client` — An already configured TypeSafe AI client
  - `passThreshold: Double` — Optional, defaults to `0.5`

### 2. `make(passThreshold: Double = 0.5): ZIO[TypeSafeAI.Client, Nothing, JevJudge]`
- **Parameters:**
  - `passThreshold: Double` — Optional, defaults to `0.5`
- Returns a ZIO effect that obtains the client from the environment

## Default Value of minConfidence

The code does not contain a `minConfidence` field. The related parameter is **`passThreshold`**, which defaults to **`0.5`** (representing 50% probability). This threshold determines when a candidate answer passes:

- Pass: when calibrated probability ≥ `passThreshold`
- Fail: when calibrated probability < `passThreshold`

**Note:** If you're looking for a different `JevJudge` class with a Builder pattern and `minConfidence` parameter, it may be from a different artifact. The class I found is part of the `zio-evals` library by James Ward, which is designed for AI evaluation scenarios using the Jev/System One model.
```

- checks: facts 1/15 missing=[groupId, artifactId, version 0.4.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Maven coordinates are incorrect, Claim that JevJudge doesn't have a Builder pattern is false, All listed methods are incorrect, Default minConfidence value is incorrect, Incorrect claim about passThreshold parameter], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant's answer is completely inconsistent with the verified reference, providing wrong coordinates, denying the existence of a Builder pattern that actually exists, listing incorrect methods, and giving a wrong default value.
- jev: FAIL 0.05 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.02, completeness.value=0.08, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.02, needs at least 0.60)
- completeness: rated "None of it" (0.24), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [groupId, artifactId, version 0.4.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]

## jevjudge-gav / mcp-toolsearch-vector

```
I now have all the information needed to answer your question.

## Maven Coordinates
```
org.springaicommunity:typesafe-spring-ai:0.4.0
```

## Builder Methods with Parameter Types

The `JevJudge.Builder` has the following public methods:

| Method | Parameters | Return Type |
|--------|------------|-------------|
| `noul` | `(String name, Noul noul, double minimum)` | `Builder` |
| `score` | `(String name, Score score, double minimum)` | `Builder` |
| `choice` | `(String name, Choice choice, String... acceptedOptions)` | `Builder` |
| `check` | `(String name, Predicate<JevJudgeInput> check, String defect)` | `Builder` |
| `criterion` | `(JevCriterion criterion)` | `Builder` |
| `minConfidence` | `(double minConfidence)` | `Builder` |
| `failOnInconclusive` | `(boolean failOnInconclusive)` | `Builder` |
| `failOnError` | `(boolean failOnError)` | `Builder` |
| `failFast` | `(boolean failFast)` | `Builder` |
| `feedbackRenderer` | `(Function<List<JevFinding>, String> feedbackRenderer)` | `Builder` |
| `build` | — | `JevJudge` |

## Default Value of `minConfidence`

**`0.6`** (60%) — defined as the constant `DEFAULT_MIN_CONFIDENCE` in the `JevJudge` class:

```java
public static final double DEFAULT_MIN_CONFIDENCE = 0.6d;
```
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer matches the reference exactly on coordinates, all builder methods with their parameter types, and the default minConfidence value, with no incorrect or invented information.
- jev: PASS 0.89 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.85, completeness.value=0.92, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jevjudge-gav / mcp-toolsearch-jev

```
Excellent! I found all the information I need from the source code. Let me summarize the findings:

## Summary

**Maven Coordinates:**
- **groupId:** `org.springaicommunity`
- **artifactId:** `typesafe-spring-ai`
- **version:** `0.4.0` (latest release)

**Full coordinates:** `org.springaicommunity:typesafe-spring-ai:0.4.0`

**JevJudge.Builder Methods with Parameter Types:**

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
| `build` | `()` (no parameters) |

**Default value of `minConfidence`:**
`0.6` (or `0.6d` as a double) - defined as the constant `DEFAULT_MIN_CONFIDENCE = 0.6d`

This represents a clear majority threshold (60%), meaning at least 60% of a score's or choice's probability must support the verdict before it is acted on.
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly provides all requested information - Maven coordinates, all Builder methods with parameters, and the default minConfidence value - matching the reference exactly with no contradictions or omissions.
- jev: PASS 0.80 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.72, completeness.value=0.87, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jackson3-ptv / mcp-toolsearch

```
Based on the Javadoc documentation for `BasicPolymorphicTypeValidator.Builder`:

## Public Methods in Jackson 3 (latest: 3.2.3)

1. `allowIfBaseType(Class<?> baseOfBase)`
2. `allowIfBaseType(String prefixForBase)`
3. `allowIfBaseType(Pattern patternForBase)`
4. `allowIfBaseType(BasicPolymorphicTypeValidator.TypeMatcher matcher)`
5. `allowIfSubType(Class<?> subTypeBase)`
6. `allowIfSubType(String prefixForSubType)`
7. `allowIfSubType(Pattern patternForSubType)`
8. `allowIfSubType(BasicPolymorphicTypeValidator.TypeMatcher matcher)`
9. `allowIfSubTypeIsArray()`
10. `allowSubTypesWithExplicitDeserializer()` ⭐ Jackson 3 only
11. `denyForExactBaseType(Class<?> baseTypeToDeny)`
12. `build()`

## Method exclusive to Jackson 3

**`allowSubTypesWithExplicitDeserializer()`** exists in Jackson 3.2.3 but does not exist in Jackson 2.22.3.

## `allowIfSubTypeIsArray()` Behavior Change

According to the documentation, the behavior changed in **2.18.8 / 3.1.4**:

| Old Behavior (before) | New Behavior (since 2.18.8 / 3.1.4) |
|-----------------------|-------------------------------------|
| Added a matcher that **approved every array regardless of element type**, which allowed attackers to bypass explicit sub-class allow-lists by wrapping a denied class in an array (e.g., `Evil[]`) | Now unwraps arrays (recursively for nested arrays) and validates the **innermost element type** against configured sub-class and sub-name matchers |
| Arrays were allowed without further validation of their component type | Arrays of primitive, abstract, or interface types are accepted without an explicit allow-list entry; other element types must be explicitly allowed |

**Key fix**: The security vulnerability (databind#5981) was addressed in 2.18.8 and 3.1.4. Callers who relied on "allow every array" must now also allow-list the specific element types they intend to accept.
```

- checks: facts 9/10 missing=[Jackson 3 groupId]
- llm: FAIL 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=false} The assistant's answer correctly lists all methods, identifies the Jackson 3 exclusive method, accurately describes the behavior change of allowIfSubTypeIsArray(), and provides the correct version numbers for the change, matching the reference answer in all factual details.
- jev: FAIL 0.86 {grounded=PASSED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.80, completeness.value=0.92, semantic_pass=true} - required_facts: missing required facts: [Jackson 3 groupId]

## jackson3-ptv / mcp-toolsearch-vector

```
Based on my analysis of the Javadoc for both Jackson versions, here's the comparison:

## Public Methods of `BasicPolymorphicTypeValidator.Builder`

### Jackson 3.x (3.2.3) - `tools.jackson.core:jackson-databind:3.2.3`:

**Public Builder Methods:**
1. `allowIfBaseType(Class<?> baseOfBase)`
2. `allowIfBaseType(Pattern patternForBase)`
3. `allowIfBaseType(String prefixForBase)`
4. `allowIfBaseType(TypeMatcher matcher)`
5. `denyForExactBaseType(Class<?> baseTypeToDeny)`
6. `allowIfSubType(Class<?> subTypeBase)`
7. `allowIfSubType(Pattern patternForSubType)`
8. `allowIfSubType(String prefixForSubType)`
9. `allowIfSubType(TypeMatcher matcher)`
10. `allowIfSubTypeIsArray()`
11. **`allowSubTypesWithExplicitDeserializer()`** ← **NEW in J3**
12. `build()`

### Jackson 2.x (2.22.3) - `com.fasterxml.jackson.core:jackson-databind:2.22.3`:

**Public Builder Methods:**
1. `allowIfBaseType(Class<?> baseOfBase)`
2. `allowIfBaseType(Pattern patternForBase)`
3. `allowIfBaseType(String prefixForBase)`
4. `allowIfBaseType(TypeMatcher matcher)`
5. `denyForExactBaseType(Class<?> baseTypeToDeny)`
6. `allowIfSubType(Class<?> subTypeBase)`
7. `allowIfSubType(Pattern patternForSubType)`
8. `allowIfSubType(String prefixForSubType)`
9. `allowIfSubType(TypeMatcher matcher)`
10. `allowIfSubTypeIsArray()`
11. `build()`

---

## Method Existence Comparison

| Method | Jackson 2.22.3 | Jackson 3.2.3 |
|--------|----------------|---------------|
| `allowSubTypesWithExplicitDeserializer()` | ❌ **Does not exist** | ✅ Exists |

**`allowSubTypesWithExplicitDeserializer()` is the only public method in Jackson 3.x that does not exist in Jackson 2.x.**

---

## Behavior Change of `allowIfSubTypeIsArray()`

According to the Javadoc for both versions, the behavior change occurred in:

### Version: **2.18.8** (Jackson 2.x) / **3.1.4** (Jackson 3.x)

**What Changed:**
- **Before:** The method added a matcher that approved **every array regardless of element type**. This allowed a security bypass where an attacker could wrap a denied class as an array (e.g., `Evil[]`) — the array matched, and the component was instantiated via plain bean deserialization without further validator invocation.

- **After:** Now the validator unwraps arrays (recursively for nested arrays) and validates the **innermost element type** against the configured sub-class matchers (`_subTypeClassMatchers` and `_subTypeNameMatchers`). Arrays of primitive, abstract, or interface element types are still accepted without an explicit allow-list entry.

**Impact:** Callers who relied on "allow every array" must now also allow-list the specific element types they intend to accept.
```

- checks: facts 10/10
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly lists all public methods, identifies the new method in Jackson 3, and accurately describes the behavior change and its versions, matching the reference in all details.
- jev: PASS 0.85 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.76, completeness.value=0.93, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jackson3-ptv / mcp-toolsearch-jev

```
I now have the full documentation for both Jackson 3 (3.2.3) and Jackson 2 (2.22.3). Let me analyze and compare:

## Summary

### Public Methods in **Jackson 3.2.3** (`tools.jackson.core:jackson-databind:3.2.3`):

Public instance methods in `BasicPolymorphicTypeValidator.Builder`:

1. `allowIfBaseType(Class<?> baseOfBase)` – Match by base type class
2. `allowIfBaseType(String prefixForBase)` – Match by base type class name prefix
3. `allowIfBaseType(Pattern patternForBase)` – Match by base type pattern
4. `allowIfBaseType(BasicPolymorphicTypeValidator.TypeMatcher matcher)` – Custom matcher for base type
5. `denyForExactBaseType(Class<?> baseTypeToDeny)` – Deny exact base type
6. `allowIfSubType(Class<?> subTypeBase)` – Allow subtype by class or subclass
7. `allowIfSubType(String prefixForSubType)` – Match by subtype name prefix
8. `allowIfSubType(Pattern patternForSubType)` – Match by subtype pattern
9. `allowIfSubType(BasicPolymorphicTypeValidator.TypeMatcher matcher)` – Custom matcher for subtype
10. **`allowIfSubTypeIsArray()`** – Enable array sub-type validation
11. **`allowSubTypesWithExplicitDeserializer()`** ← **NEW IN J3**
12. `build()` – Build the validator

### Jackson 2.22.3 (`com.fasterxml.jackson.core:jackson-databind:2.22.3`):

All the same methods **except**:
- `allowSubTypesWithExplicitDeserializer()` – **DOES NOT EXIST IN 2.22.3**

### `allowIfSubTypeIsArray()` Behavior Change

According to the Javadoc in both versions:

- **2.18.8+** and **3.1.4+**: The behavior changed ([databind#5981]). Prior versions added a matcher that **approved every array regardless of element type**, which allowed attackers to bypass an explicit sub-class allow-list by wrapping a denied class as an array (e.g., `Evil[]` — the array matched, and the component was instantiated via plain bean deserialization without further validator invocation).

- **After the fix**: The validator now unwraps arrays (recursively for nested arrays) and validates the innermost element type against the configured sub-class matchers. Arrays of primitives, abstract, or interface element types are accepted without explicit allow-list entries.

### Answer

**Method that does NOT exist in Jackson 2.x:**
- `allowSubTypesWithExplicitDeserializer()` — This method was added in Jackson 3 and is not present in any Jackson 2.x release.

**Behavior change in `allowIfSubTypeIsArray()`:**
- Changed in: **2.18.8** (for Jackson 2.x) and **3.1.4** (for Jackson 3.x)
- **Old behavior**: Approved every array regardless of element type (vulnerable: allowed bypassing allow-lists via array wrapper)
- **New behavior**: Validates the innermost element type against configured sub-class/name matchers; only primitive/abstract/interface element types are auto-accepted
```

- checks: facts 10/10
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly lists all public methods, identifies the missing method in Jackson 2.x, and accurately describes the behavior change of allowIfSubTypeIsArray() with correct version numbers, matching the reference in all factual details.
- jev: FAIL 0.69 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.53, completeness.value=0.85, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.53, needs at least 0.60)

## spring-ai-hostedtool / mcp-toolsearch

```
UnauthorizedException: 401: The security token included in the request is expired
```

- checks: facts 0/8 missing=[version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.40 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.80, completeness.value=0.00, semantic_pass=false} - completeness: rated "None of it" (0.01), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]

## spring-ai-hostedtool / mcp-toolsearch-vector

```
IllegalStateException: Cohere embed on Bedrock failed (403): {"Message":"Authentication failed: Please make sure your API Key is valid."}
```

- checks: facts 0/8 missing=[version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.45 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.90, completeness.value=0.00, semantic_pass=false} - completeness: rated "None of it" (0.00), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]

## spring-ai-hostedtool / mcp-toolsearch-jev

```
UnauthorizedException: 401: The security token included in the request is expired
```

- checks: facts 0/8 missing=[version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.45 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.89, completeness.value=0.00, semantic_pass=false} - completeness: rated "None of it" (0.00), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]

## agentutils-shell-trap / mcp-toolsearch

```
UnauthorizedException: 401: The security token included in the request is expired
```

- checks: facts 0/5 missing=[version 0.13.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.33 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, trap_handled=FAILED, grounded.value=0.90, completeness.value=0.00, trap_handled.value=0.08, semantic_pass=false} - completeness: rated "None of it" (0.00), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 0.13.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- trap_handled: The answer describes or invents the non-existent API (scored 0.08, needs at least 0.70)

## agentutils-shell-trap / mcp-toolsearch-vector

```
IllegalStateException: Cohere embed on Bedrock failed (403): {"Message":"Authentication failed: Please make sure your API Key is valid."}
```

- checks: facts 0/5 missing=[version 0.13.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.33 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, trap_handled=FAILED, grounded.value=0.92, completeness.value=0.00, trap_handled.value=0.08, semantic_pass=false} - completeness: rated "None of it" (0.01), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 0.13.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- trap_handled: The answer describes or invents the non-existent API (scored 0.08, needs at least 0.70)

## agentutils-shell-trap / mcp-toolsearch-jev

```
UnauthorizedException: 401: The security token included in the request is expired
```

- checks: facts 0/5 missing=[version 0.13.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- llm: ERR {} 401: The security token included in the request is expired
- jev: FAIL 0.33 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, trap_handled=FAILED, grounded.value=0.92, completeness.value=0.00, trap_handled.value=0.07, semantic_pass=false} - completeness: rated "None of it" (0.00), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 0.13.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- trap_handled: The answer describes or invents the non-existent API (scored 0.07, needs at least 0.70)
