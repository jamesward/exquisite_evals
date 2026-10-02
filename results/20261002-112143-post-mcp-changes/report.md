# Eval results

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | base | 2/15 | FAIL 0.00 | FAIL 0.05 | 119 | 2246 | 0 | 1 | 0 | 17.5 |
| jevjudge-gav | shell | 1/15 | FAIL 0.00 | FAIL 0.11 | 21296 | 816 | 0 | 5 | 4 | 9.4 |
| jevjudge-gav | web-brave | 14/15 | FAIL 0.25 | FAIL 0.30 | 244174 | 3950 | 24587 | 27 | 25 | 70.3 |
| jevjudge-gav | mcp | 15/15 | PASS 1.00 | PASS 0.85 | 119636 | 5006 | 0 | 24 | 23 | 51.2 |
| jevjudge-gav | mcp-toolsearch | 15/15 | PASS 1.00 | PASS 0.93 | 35233 | 1909 | 0 | 12 | 10 | 23.2 |
| jevjudge-gav | mcp-toolsearch-vector | 1/15 | FAIL 0.00 | FAIL 0.03 | 31808 | 2104 | 1172 | 17 | 15 | 35.8 |
| jackson3-ptv | base | 3/10 +11 invented | FAIL 0.08 | FAIL 0.06 | 126 | 3711 | 0 | 1 | 0 | 21.8 |
| jackson3-ptv | shell | 4/10 +1 invented (budget) | FAIL 0.08 | FAIL 0.10 | 296909 | 7932 | 0 | 27 | 25 | 227.5 |
| jackson3-ptv | web-brave | 5/10 +4 invented | FAIL 0.08 | FAIL 0.17 | 242420 | 7095 | 5681 | 27 | 25 | 113.9 |
| jackson3-ptv | mcp | 6/10 | FAIL 0.50 | FAIL 0.23 | 175426 | 6723 | 0 | 27 | 25 | 78.2 |
| jackson3-ptv | mcp-toolsearch | 10/10 | FAIL 0.25 | FAIL 0.32 | 56426 | 3772 | 0 | 11 | 8 | 34.8 |
| jackson3-ptv | mcp-toolsearch-vector | 4/10 +3 invented | FAIL 0.00 | FAIL 0.06 | 47848 | 4252 | 1187 | 19 | 17 | 42.9 |
| spring-ai-hostedtool | base | 1/7 | FAIL 0.00 | FAIL 0.12 | 102 | 1328 | 0 | 1 | 0 | 10.9 |
| spring-ai-hostedtool | shell | 6/7 | FAIL 1.00 | FAIL 0.53 | 120503 | 3921 | 0 | 21 | 20 | 207.7 |
| spring-ai-hostedtool | web-brave | 7/7 | FAIL 0.08 | FAIL 0.26 | 239295 | 4379 | 22663 | 27 | 25 | 74.2 |
| spring-ai-hostedtool | mcp | 1/7 | FAIL 0.00 | FAIL 0.26 | 161075 | 4022 | 0 | 28 | 25 | 72.9 |
| spring-ai-hostedtool | mcp-toolsearch | 1/7 | FAIL 0.00 | FAIL 0.05 | 81667 | 5271 | 0 | 27 | 21 | 56.2 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 0/7 (run error) | FAIL 0.33 | FAIL 0.37 | 114801 | 4621 | 1180 | 26 | 24 | 62.4 |
| agentutils-shell-trap | base | 1/5 +2 invented | FAIL 0.13 | FAIL 0.06 | 128 | 2990 | 0 | 1 | 0 | 15.1 |
| agentutils-shell-trap | shell | 1/5 +1 invented (budget) | FAIL 0.13 | FAIL 0.09 | 282007 | 4335 | 0 | 26 | 25 | 209.1 |
| agentutils-shell-trap | web-brave | 3/5 +1 invented | FAIL 0.19 | FAIL 0.16 | 235019 | 5215 | 7777 | 29 | 25 | 91.7 |
| agentutils-shell-trap | mcp | 5/5 (budget) | PASS 1.00 | PASS 0.78 | 273222 | 5977 | 0 | 25 | 24 | 71.0 |
| agentutils-shell-trap | mcp-toolsearch | 5/5 | PASS 1.00 | FAIL 0.81 | 102651 | 4904 | 0 | 27 | 16 | 59.2 |
| agentutils-shell-trap | mcp-toolsearch-vector | 0/5 | FAIL 0.50 | FAIL 0.25 | 182060 | 4195 | 1185 | 21 | 19 | 52.2 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| base | 0/4 | 0/4 | 0/4 | 10750 | 16.4 |
| shell | 0/4 | 0/4 | 0/4 | 737719 | 163.4 |
| web-brave | 1/4 | 0/4 | 0/4 | 1042255 | 87.5 |
| mcp | 2/4 | 2/4 | 2/4 | 751087 | 68.3 |
| mcp-toolsearch | 3/4 | 2/4 | 1/4 | 291833 | 43.4 |
| mcp-toolsearch-vector | 0/4 | 0/4 | 0/4 | 396413 | 48.3 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 4/24 | 31586 | 13820 | 96.1 | 0 |
| jev | 3/24 | 65420 | 1050 | 7.2 | 0 |

Agreement: llm vs jev 23/24 | semantic-only llm vs jev 22/24 | llm-semantic vs code checks 21/24 | jev-semantic vs code checks 21/24

## jevjudge-gav / base

```
We need to browse.Let's search.Let's do a web search.Search term: "JevJudge Maven".Below is the information that is currently published on Maven Central for the **`JevJudge`** class (as of the time of writing – 2026‑10‑02).

| Item | Value |
|------|-------|
| **groupId** | `org.jvnet.jev` |
| **artifactId** | `jev‑judge` |
| **latest version** | `1.4.3` |
| **Maven coordinates** | `org.jvnet.jev:jev‑judge:1.4.3` |
| **Repository URL** | <https://repo1.maven.org/maven2/org/jvnet/jev/jev‑judge/1.4.3/> |

---

## `JevJudge.Builder` – public API

The `JevJudge` class is instantiated through an inner static **`Builder`** class.  
All builder‑methods are **fluent** (they return the builder itself) and are listed in the order they appear in the source code (as of version 1.4.3).

| Builder method | Parameter types | Return type |
|----------------|----------------|-------------|
| `withMinConfidence(double minConfidence)` | `double` | `Builder` |
| `withMaxConfidence(double maxConfidence)` | `double` | `Builder` |
| `withThreshold(int threshold)` | `int` | `Builder` |
| `withLabels(Collection<String> labels)` | `java.util.Collection<? extends java.lang.String>` | `Builder` |
| `withLabel(String label)` | `java.lang.String` | `Builder` |
| `withEvidenceProvider(EvidenceProvider provider)` | `org.jvnet.jev.EvidenceProvider` | `Builder` |
| `withJudgmentStrategy(JudgmentStrategy strategy)` | `org.jvnet.jev.JudgmentStrategy` | `Builder` |
| `withAllowPartial(boolean allowPartial)` | `boolean` | `Builder` |
| `withTimeout(java.time.Duration timeout)` | `java.time.Duration` | `Builder` |
| `withExecutor(java.util.concurrent.Executor executor)` | `java.util.concurrent.Executor` | `Builder` |
| `withLogger(java.util.logging.Logger logger)` | `java.util.logging.Logger` | `Builder` |
| `build()` | *(no parameters)* | `JevJudge` |

> **Note:** All of the above methods are **public** and are the only ones that appear in the generated Javadoc for `JevJudge.Builder`. No other overloads or hidden methods are present in version 1.4.3.

---

## Default value of `minConfidence`

If the caller does **not** invoke `withMinConfidence(...)`, the builder uses the library’s built‑in default:

```java
private static final double DEFAULT_MIN_CONFIDENCE = 0.65;
```

Therefore, the **default value of `minConfidence` is `0.65`** (i.e., 65 % confidence).

---

### How to use the builder (example)

```java
JevJudge judge = JevJudge.builder()
        // minConfidence is omitted → defaults to 0.65
        .withMaxConfidence(0.95)
        .withThreshold(3)
        .withLabel("spam")
        .withAllowPartial(true)
        .build();
```

The example demonstrates that when `withMinConfidence` is not called, the resulting `JevJudge` instance will automatically operate with a minimum confidence of **0.65**.
```

- checks: facts 2/15 missing=[groupId, artifactId, version 0.3.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[groupId "org.jvnet.jev" is incorrect, artifactId "jev-judge" is incorrect, version "1.4.3" is incorrect, Maven coordinates "org.jvnet.jev:jev-judge:1.4.3" are fabricated, Builder methods "withMinConfidence", "withMaxConfidence", "withThreshold", "withLabels", "withLabel", "withEvidenceProvider", "withJudgmentStrategy", "withAllowPartial", "withTimeout", "withExecutor", "withLogger" are not present in the real JevJudge.Builder, Default minConfidence value 0.65 is incorrect (actual default is 0.6), Repository URL and other listed metadata are unrelated to the actual library], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant provided entirely different Maven coordinates, builder API, and default value than those given in the reference, making the answer factually inconsistent and missing all required information.
- jev: FAIL 0.05 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.01, completeness.value=0.08, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "None of it" (0.25), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [groupId, artifactId, version 0.3.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer]

## jevjudge-gav / shell

```
I’m sorry—I wasn’t able to locate any artifact on Maven Central that contains a class named **`JevJudge`**, nor could I find its source code (e.g., on GitHub) to inspect its builder API or the default value of `minConfidence`. If you have additional information such as the full package name, a GitHub repository URL, or an alternate spelling of the artifact, please share it and I can look it up again.
```

- checks: facts 1/15 missing=[groupId, artifactId, version 0.3.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claimed that no Maven artifact contains a class named JevJudge, contrary to the reference which provides the coordinates org.springaicommunity:typesafe-spring-ai:0.3.0], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer provides no coordinates, method list, or default value and incorrectly asserts the class does not exist, which contradicts the verified reference.
- jev: FAIL 0.11 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.21, completeness.value=0.00, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.21, needs at least 0.70)
- completeness: rated "None of it" (0.01), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [groupId, artifactId, version 0.3.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]

## jevjudge-gav / web-brave

```
**Maven coordinates (latest released version)**  
```
org.springaicommunity.typesafe:typesafe-judge:1.0.0
```  

*The source file for `JevJudge` contains a Maven‑coordinates comment that lists the above GAV (groupId `org.springaicommunity.typesafe`, artifactId `typesafe-judge`, version `1.0.0`).*  

**Builder API – methods and parameter types**

| Builder method | Parameter types |
|----------------|-----------------|
| `noul(String name, Noul question, double minimum)` | `String`, `Noul`, `double` |
| `score(String name, Score question, double minimum)` | `String`, `Score`, `double` |
| `choice(String name, Choice question, String... acceptedOptions)` | `String`, `Choice`, `String[]` (var‑args) |
| `check(String name, Predicate<JevJudgeInput> check, String defect)` | `String`, `Predicate<JevJudgeInput>`, `String` |
| `criterion(JevCriterion criterion)` | `JevCriterion` |
| `minConfidence(double minConfidence)` | `double` |
| `failOnError(boolean failOnError)` | `boolean` |
| `failFast(boolean failFast)` | `boolean` |
| `failOnInconclusive(boolean failOnInconclusive)` | `boolean` |
| `feedbackRenderer(Function<List<JevFinding>, String> renderer)` | `Function<List<JevFinding>, String>` |
| `build()` | *(no parameters)* |

**Default value of `minConfidence`**  
The Builder’s `minConfidence` defaults to **`0.6`** when not explicitly set.

---

**Sources**

- [JevJudge source file – Maven coordinates comment](https://spring-ai-community.github.io/spring-ai-typesafe/latest/judge/JevJudge/)  
- [JevJudge documentation – Builder method table and default `minConfidence`](https://spring-ai-community.github.io/spring-ai-typesafe/latest/judge/JevJudge/)  
- [Parent POM of the project (shows current project version)](https://raw.githubusercontent.com/spring-ai-community/spring-ai-typesafe/main/typesafe-spring-ai/pom.xml)
```

- checks: facts 14/15 missing=[version 0.3.0]
- llm: FAIL 0.25 {factually_consistent=false, completeness=3, hallucinations=[Incorrect Maven coordinates (groupId, artifactId, version) – the answer gives org.springaicommunity.typesafe:typesafe-judge:1.0.0 instead of the correct org.springaicommunity:typesafe-spring-ai:0.3.0, Claim that the source file contains a Maven‑coordinates comment with the provided GAV, which is not supported by the reference], trap_handled=null, semantic_pass=false, code_checks_pass=false} The methods list and default value are correct, but the Maven coordinates are wrong, making the answer factually inconsistent and partially incomplete.
- jev: FAIL 0.30 {grounded=FAILED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.03, completeness.value=0.58, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.03, needs at least 0.70)
- required_facts: missing required facts: [version 0.3.0]

## jevjudge-gav / mcp

```
**Maven coordinate (latest release)**  
`org.springaicommunity:typesafe‑spring‑ai:0.3.0` [[1]](function://functions.get_latest_version)

**`JevJudge.Builder` – public instance methods**

| Method | Parameter types |
|--------|-----------------|
| `noul(String name, Noul noul, double minimum)` | `java.lang.String`, `org.springaicommunity.typesafe.question.Noul`, `double` |
| `score(String name, Score score, double minimum)` | `java.lang.String`, `org.springaicommunity.typesafe.question.Score`, `double` |
| `choice(String name, Choice choice, String… acceptedOptions)` | `java.lang.String`, `org.springaicommunity.typesafe.question.Choice`, `java.lang.String[]` |
| `check(String name, Predicate<JevJudgeInput> check, String defect)` | `java.lang.String`, `java.util.function.Predicate<org.springaicommunity.typesafe.judge.JevJudgeInput>`, `java.lang.String` |
| `criterion(JevCriterion criterion)` | `org.springaicommunity.typesafe.judge.JevCriterion` |
| `minConfidence(double minConfidence)` | `double` |
| `failOnInconclusive(boolean failOnInconclusive)` | `boolean` |
| `failOnError(boolean failOnError)` | `boolean` |
| `failFast(boolean failFast)` | `boolean` |
| `feedbackRenderer(Function<List<JevFinding>, String> feedbackRenderer)` | `java.util.function.Function<java.util.List<org.springaicommunity.typesafe.judge.JevFinding>, java.lang.String>` |
| `build()` | *none* |

These are the methods listed in the Javadoc for `JevJudge.Builder` [[2]](function://functions.get_javadoc_symbol).

**Default value of `minConfidence`**

The class defines a constant:  

```java
public static final double DEFAULT_MIN_CONFIDENCE = 0.6d;
```  

The builder’s `minConfidence` field is initialized with this constant, so the default is **0.6** [[3]](function://functions.get_source_file).
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The answer provides the correct Maven coordinate, lists all Builder methods with matching signatures, and correctly states the default minConfidence value of 0.6.
- jev: PASS 0.85 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.85, completeness.value=0.85, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jevjudge-gav / mcp-toolsearch

```
**Maven coordinate (latest release)**  
```
org.springaicommunity:typesafe-spring-ai:0.3.0
```

**`JevJudge.Builder` – instance methods and their parameter types**

| Method | Parameter(s) (type – name) |
|--------|----------------------------|
| `build()` | *(none)* |
| `check(String name, Predicate<JevJudgeInput> check, String defect)` | `java.lang.String name`, `java.util.function.Predicate<org.springaicommunity.typesafe.judge.JevJudgeInput> check`, `java.lang.String defect` |
| `choice(String name, Choice choice, String... acceptedOptions)` | `java.lang.String name`, `org.springaicommunity.typesafe.question.Choice choice`, `java.lang.String... acceptedOptions` |
| `criterion(JevCriterion criterion)` | `org.springaicommunity.typesafe.judge.JevCriterion criterion` |
| `failFast(boolean failFast)` | `boolean failFast` |
| `failOnError(boolean failOnError)` | `boolean failOnError` |
| `failOnInconclusive(boolean failOnInconclusive)` | `boolean failOnInconclusive` |
| `feedbackRenderer(Function<List<JevFinding>, String> feedbackRenderer)` | `java.util.function.Function<java.util.List<org.springaicommunity.typesafe.judge.JevFinding>, java.lang.String> feedbackRenderer` |
| `minConfidence(double minConfidence)` | `double minConfidence` |
| `noul(String name, Noul noul, double minimum)` | `java.lang.String name`, `org.springaicommunity.typesafe.question.Noul noul`, `double minimum` |
| `score(String name, Score score, double minimum)` | `java.lang.String name`, `org.springaicommunity.typesafe.question.Score score`, `double minimum` |

*(The static factory method `JevJudge.builder(TypeSafeClient)` is not part of the Builder instance.)*

**Default value of `minConfidence`**  
The constant `JevJudge.DEFAULT_MIN_CONFIDENCE` is **0.6** (i.e., 60 % confidence).
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The answer supplies the correct Maven coordinates, enumerates all Builder methods with accurate signatures, and reports the default minConfidence of 0.6, matching the reference.
- jev: PASS 0.93 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.89, completeness.value=0.97, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jevjudge-gav / mcp-toolsearch-vector

```
I wasn’t able to locate any artifact on Maven Central that contains a class named **`JevJudge`**. Searches for the exact class name, variations in spelling and case, and even broader‑term queries (e.g., “judge”, “jev”, “jev judge”, “JEvJudge”) all returned no matching results from the Maven Central index.  

**Conclusion:**  
* There is no publicly published Maven Central artifact (i.e., no `groupId:artifactId:version`) that provides a `JevJudge` class, so I cannot list a Builder’s methods or its default `minConfidence` value. If the class comes from a private repository, an internal company library, or a non‑Maven‑Central source, you’ll need to consult that specific source directly.
```

- checks: facts 1/15 missing=[groupId, artifactId, version 0.3.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims that no Maven Central artifact contains a JevJudge class, States that it cannot provide coordinates, builder methods, or default minConfidence], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer contradicts the verified reference which provides exact Maven coordinates, a full list of Builder methods, and the default minConfidence value, and the assistant gives no correct information.
- jev: FAIL 0.03 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.06, completeness.value=0.01, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.06, needs at least 0.70)
- completeness: rated "None of it" (0.02), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [groupId, artifactId, version 0.3.0, default minConfidence 0.6, noul, score, choice, check, criterion, failOnInconclusive, failOnError, failFast, feedbackRenderer, build]

## jackson3-ptv / base

```
## 1.  Public‑API of **`BasicPolymorphicTypeValidator.Builder`** in the *Jackson 3* line  

(Version 3.0.0 – 3.1.x – 3.2.x – the latest at the time of writing)

| # | Method signature (public) | Short description |
|---|----------------------------|--------------------|
| 1 | `Builder allowIfBaseType(Class<?> baseType)` | Accept any sub‑type **provided the declared base type** of the property is `baseType` (or a subclass of it). |
| 2 | `Builder allowIfBaseType(String baseClassName)` | Same as above, but the base type is supplied by its fully‑qualified class name (useful when the class may not be on the class‑path at compile time). |
| 3 | `Builder allowIfSubType(Class<?> subType)` | Accept a concrete sub‑type **anywhere** the validator sees it, regardless of the declared base type. |
| 4 | `Builder allowIfSubType(String subClassName)` | Same as above, using a class‑name string. |
| 5 | `Builder allowIfBaseTypeIsArray()` | Accept any **array** type **only when the declared base type is also an array** (e.g. `String[]` when the declared type is `Object[]`). |
| 6 | `Builder allowIfBaseTypeIsCollection()` | Accept any `java.util.Collection` implementation **only when the declared base type is a collection**. |
| 7 | `Builder allowIfBaseTypeIsMap()` | Accept any `java.util.Map` implementation **only when the declared base type is a map**. |
| 8 | `Builder allowIfBaseTypeIsEnum()` | Accept any `enum` **only when the declared base type is an enum**. |
| 9 | `Builder allowIfBaseTypeIsReferenceType()` | Accept any reference‑type (i.e. any non‑primitive, non‑array) **only when the declared base type is a reference type**. |
|10 | `Builder allowIfSubTypeIsArray()` | Accept any **array** sub‑type (e.g. `int[]`, `String[]`) **when the declared base type is *not* a concrete class** – the exact semantics are explained in the next section. |
|11 | `Builder allowIfSubTypeIsCollection()` | Accept any `Collection` sub‑type when the base type is not a concrete class. |
|12 | `Builder allowIfSubTypeIsMap()` | Accept any `Map` sub‑type when the base type is not a concrete class. |
|13 | `Builder allowIfSubTypeIsEnum()` | Accept any `enum` sub‑type when the base type is not a concrete class. |
|14 | `Builder allowIfSubTypeIsReferenceType()` | Accept any reference‑type sub‑type when the base type is not a concrete class. |
|15 | `Builder denyIfBaseType(Class<?> baseType)` | Explicitly **reject** values whose declared base type matches `baseType`. |
|16 | `Builder denyIfBaseType(String baseClassName)` | Same as above, using a class name. |
|17 | `Builder denyIfSubType(Class<?> subType)` | Explicitly **reject** a concrete sub‑type. |
|18 | `Builder denyIfSubType(String subClassName)` | Same as above, using a class name. |
|19 | `BasicPolymorphicTypeValidator build()` | Produce the immutable validator instance. |

> **Note** – The builder also inherits the three “fluent‑style” methods from `Object` (`toString()`, `hashCode()`, `equals(Object)`), but those are not considered part of the Jackson‑specific public API.

---

## 2.  Which of the above methods **does NOT exist** in the *latest* Jackson 2.x line?

The **only method that is new to the Jackson 3 series** is:

| Method | Presence in Jackson 2.x |
|--------|------------------------|
| `allowIfBaseTypeIsArray()` | **Absent** (not part of any 2.x release up to 2.15) |

All the other builder methods (`allowIfBaseType`, `allowIfSubType`, the “*IsCollection*”, “*IsMap*”, “*IsEnum*”, “*IsReferenceType*”, the `deny…` overloads, and `allowIfSubTypeIsArray()` etc.) already existed in the 2.x line, most of them being introduced as far back as Jackson 2.12 (when the `BasicPolymorphicTypeValidator` API was first added).

---

## 3.  Behavioural change of **`allowIfSubTypeIsArray()`**

### 3.1  What the method does (high‑level)

`allowIfSubTypeIsArray()` tells the validator to **grant permission** when the **actual runtime type** that is being deserialized is an *array* (e.g. `int[]`, `String[]`, `MyPojo[]`).  
The method does **not** look at the *declared* base type of the property; it only inspects the *actual* type that appears in the JSON type‑id.

### 3.2  How the semantics changed across versions

| Jackson version | Behaviour of `allowIfSubTypeIsArray()` |
|-----------------|----------------------------------------|
| **2.12.x – 2.13.x** (first introduction) | The method **unconditionally** allowed *any* array type **even when the declared base type was a concrete, non‑array class**. Example: If a property was declared as `java.util.List<java.lang.Object>` and the incoming JSON contained a type‑id for `String[]`, the validator would **accept** it because the sub‑type is an array. This behaviour turned out to be too permissive – it effectively let an attacker bypass a “allow only `List`” rule by supplying an array. |
| **2.14.0 (released March 2023)** | The logic was tightened: `allowIfSubTypeIsArray()` now **only succeeds if the *declared* base type is also an array type** (or `Object`). In practice the method checks that `baseType.isArray()` **or** that the base type is `Object` (the most generic type). If the base type is a concrete non‑array class (`java.util.List`, a POJO, etc.) the validator will **reject** the array sub‑type, regardless of the `allowIfSubTypeIsArray()` flag. |
| **3.0.0 (released June 2023) and later** | The 2.14‑behaviour is retained unchanged. The Javadoc of the method now explicitly states: *“Allowed only when the declared base type is array‑compatible; otherwise the validator will fall back to the normal sub‑type checks.”* No additional behavioural tweaks were introduced after 2.14. |

### 3.3  Why the change was made

* **Security** – The original behaviour allowed a *type‑id injection* whereby an attacker could supply an array (or a multi‑dimensional array) and escape a whitelist that only mentioned collection types.
* **Predictability** – Users expected *“allow if sub‑type is array”* to be scoped to the same “array‑family” they declared in their POJOs. Making the rule base‑type aware aligns the validator with the “base‑type / sub‑type” mental model used by the rest of the API (`allowIfBaseType…`, `allowIfSubType…`).

### 3.4  Practical example

```java
// POJO definition
class Wrapper {
    // version 2.13.x: declared as List<Object>
    public List<Object> items;
}

// JSON payload (type id for String[])
{
  "@type":"[Ljava.lang.String;",   // i.e. String[]
  "items":[ "a","b" ]
}
```

* **Jackson 2.13.x** – `allowIfSubTypeIsArray()` would let this JSON through, even though the declared type is `List`.
* **Jackson 2.14.0+ / 3.x** – Validation fails because the declared base type (`java.util.List`) is **not** an array. The only way to make it pass is either:
  * Declare the property as `Object[]` (or `Object`), **or**
  * Add an explicit `allowIfBaseTypeIsArray()` rule (new in 3.x) to permit the base‑type‑array case.

---

## 4.  TL;DR Summary

| Item | Jackson 3 (latest) | Jackson 2 (latest) | Comments |
|------|--------------------|--------------------|----------|
| Public builder methods | All 19 methods listed above (plus the inherited `Object` methods) | Same set **except** `allowIfBaseTypeIsArray()` | `allowIfBaseTypeIsArray()` is the only method missing in the 2.x line. |
| `allowIfSubTypeIsArray()` behaviour | **Only** permits arrays when the **declared base type is also an array (or `Object`)**. | Up to 2.13.x it **unconditionally** permitted any array, regardless of the base type. | Behaviour changed in **Jackson 2.14.0** (March 2023) and kept unchanged in all 3.x releases. |

These differences are the ones you will hit if you migrate an application from the 2.x line to the 3.x line while using a custom `BasicPolymorphicTypeValidator` (or the default `PolymorphicTypeValidator.builder()` that the `ObjectMapper` creates for you). Adjust your validator configuration accordingly – especially if you relied on the older “any‑array‑allowed” semantics.
```

- checks: facts 3/10 missing=[Jackson 3 groupId, Jackson 3 version 3.2.3, Jackson 2 version 2.22.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8] hallucinated=[allowIfBaseTypeIsArray, allowIfBaseTypeIsCollection, allowIfBaseTypeIsMap, allowIfBaseTypeIsEnum, allowIfBaseTypeIsReferenceType, allowIfSubTypeIsCollection, allowIfSubTypeIsMap, allowIfSubTypeIsEnum, allowIfSubTypeIsReferenceType, denyIfBaseType, denyIfSubType]
- llm: FAIL 0.08 {factually_consistent=false, completeness=1, hallucinations=[allowIfBaseTypeIsArray(), allowIfBaseTypeIsCollection(), allowIfBaseTypeIsMap(), allowIfBaseTypeIsEnum(), allowIfBaseTypeIsReferenceType(), allowIfSubTypeIsCollection(), allowIfSubTypeIsMap(), allowIfSubTypeIsEnum(), allowIfSubTypeIsReferenceType(), denyIfBaseType(...) overloads, denyIfSubType(...) overloads, claim that only allowIfBaseTypeIsArray() is missing in Jackson 2.x, incorrect version numbers for behaviour change (2.14.0 and 3.0.0 instead of 2.18.8 and 3.1.4), incorrect description of allowIfSubTypeIsArray() semantics], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer lists many methods that do not exist in the reference, omits the correct missing method, and gives wrong version information for the behavior change.
- jev: FAIL 0.06 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, grounded.value=0.01, completeness.value=0.10, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "None of it" (0.31), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [Jackson 3 groupId, Jackson 3 version 3.2.3, Jackson 2 version 2.22.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8]
- no_invented_names: invented names: [allowIfBaseTypeIsArray, allowIfBaseTypeIsCollection, allowIfBaseTypeIsMap, allowIfBaseTypeIsEnum, allowIfBaseTypeIsReferenceType, allowIfSubTypeIsCollection, allowIfSubTypeIsMap, allowIfSubTypeIsEnum, allowIfSubTypeIsReferenceType, denyIfBaseType, denyIfSubType]

## jackson3-ptv / shell

```
**1.  Public API of `BasicPolymorphicTypeValidator.Builder` in the *current* Jackson 3 line**

In the source tree that ships with the first “Jackson 3” artefact (the `jackson‑databind` version that carries the `3.x` major version number) the builder class is declared as a **public static** inner class of `BasicPolymorphicTypeValidator`.  
All of the methods that are declared **public** (i.e. part of the external API) are:

| # | Method signature (as it appears in the Javadoc / source) | Short description |
|---|------------------------------------------------------------|-------------------|
| 1 | `public Builder allowIfSubType(Class<?> subType)` | Accept a concrete sub‑type (by `Class`). |
| 2 | `public Builder allowIfSubType(String subTypeName)` | Accept a concrete sub‑type (by fully‑qualified class name). |
| 3 | `public Builder allowIfBaseType(Class<?> baseType)` | Accept any sub‑type of the given base type (by `Class`). |
| 4 | `public Builder allowIfBaseType(String baseTypeName)` | Accept any sub‑type of the given base type (by fully‑qualified class name). |
| 5 | `public Builder allowIfSubTypeIsArray()` | Accept **any** array type, regardless of component type. |
| 6 | `public Builder allowIfBaseTypeIsArray()` | Accept any array whose *component* type is a subclass of the supplied base type. |
| 7 | `public BasicPolymorphicTypeValidator build()` | Construct the immutable validator instance. |

(There are also the usual *fluent* return‑type‑convenience methods (`self()` etc.) that are `protected`/package‑private, and the default constructor, but the seven entries above are the only **public** members that a caller can invoke.)

---

**2.  Which of those methods *do not exist* in the latest Jackson 2.x release?**

The *last* 2‑series release (at the time of writing – `2.22.3`) still ships the same `BasicPolymorphicTypeValidator.Builder` class, **except** that it **does not contain** the method

```
public Builder allowIfSubTypeIsArray()
```

All other six public methods listed above are present in the 2.x line (they were added in the 2.13 series, see the “Jackson 2.13 – Polymorphic type validator” changelog).

Thus, the *only* method that is new to the 3.x line is **`allowIfSubTypeIsArray()`**.

---

**3.  Behaviour change of `allowIfSubTypeIsArray()`**

| Version(s) | Behaviour of `allowIfSubTypeIsArray()` |
|------------|----------------------------------------|
| **Jackson 2.13** (first version where the method appeared) | The method *unconditionally* allowed **any** array type, i.e. if the validator was built with `allowIfSubTypeIsArray()` then a polymorphic type id that resolves to `String[]`, `int[]`, `MyPojo[]`, etc. would always be considered safe, **even when the component type itself would be disallowed by other rules**. |
| **Jackson 2.14** (and all subsequent 2.x releases) | The method’s semantics were **tightened**: an array is only allowed when the *component type* would be allowed by the other rules that are in effect on the builder. In practice this means that `allowIfSubTypeIsArray()` now works as a *shortcut* for “allow the array *iff* its element type is allowed”. If the element type is blocked (e.g. a known insecure class), the array will also be blocked. |
| **Jackson 3.0** (current major release) | The same tightened semantics from 2.14 are retained. The method is now documented as “*Allow any array type whose element type is permitted by the other constraints of the builder*”. No regression to the “allow‑everything‑array” behaviour was re‑introduced. |

*Why the change?*  
A JIRA issue (see **[databind‑3805] “Array‑type polymorphic handling should respect element type restrictions”**) reported that allowing *any* array opened a security hole: an attacker could send a payload such as `java.lang.String[]` where the element type (`String`) is harmless, but the *array* itself could be used as a wrapper for another vulnerable type. The fix was to make the validator look at the array’s component type and apply the same allow/deny checks that are used for non‑array types.

---

### What could not be verified directly

| Item | Reason |
|------|--------|
| *Exact source of the 3.x `BasicPolymorphicTypeValidator.Builder`* | The Maven repository does not yet host any `jackson‑databind` artefact with a `3.x` version, and the tag/branch named “3.0” is not present in the official GitHub repository. The method list above is therefore based on the **public Javadoc** that ships with the preview release (and on the diff that was announced in the Jackson 3 release notes). |
| *Exact version where the behaviour of `allowIfSubTypeIsArray()` changed from “allow any array” to “respect component‑type rules”* | The change is recorded in the **Jackson 2.14** release notes and the corresponding JIRA entry, but I could not extract the exact commit hash from the repository (no network access left). The description above reflects the information that is publicly available in the changelog. |
| *Presence of `allowIfBaseTypeIsArray()` in 2.x* | This method has been part of the builder since the 2.13 series (it appears in the 2.22.3 source JAR) and was not a point of contention, so its existence is taken as given. |

---

### TL;DR

* **Public builder methods in Jackson 3**: `allowIfSubType(Class)`, `allowIfSubType(String)`, `allowIfBaseType(Class)`, `allowIfBaseType(String)`, `allowIfSubTypeIsArray()`, `allowIfBaseTypeIsArray()`, `build()`.  
* **Missing in Jackson 2.x**: `allowIfSubTypeIsArray()`.  
* **Behaviour of `allowIfSubTypeIsArray()`**:  
  * **Jackson 2.13** – allowed every array type, regardless of the element class.  
  * **Jackson 2.14 → Jackson 3.0** – now only allows an array when the element type is itself allowed by the other builder constraints (i.e. it respects component‑type restrictions).  

If you need the exact source code for the 3.x builder, you’ll have to wait until the `jackson‑databind` 3.x artefact is published to Maven Central (or grab the snapshot from the Jackson GitHub repository). The method list and behavioural notes above are drawn from the public release notes and the 2.x source that is currently available.
```

- checks: facts 4/10 missing=[Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8] hallucinated=[allowIfBaseTypeIsArray]
- llm: FAIL 0.08 {factually_consistent=false, completeness=1, hallucinations=[Listed method allowIfBaseTypeIsArray() which does not exist in Jackson 3.2.3, Omitted overloads (Pattern, TypeMatcher) for allowIfBaseType and allowIfSubType, Omitted allowSubTypesWithExplicitDeserializer() and denyForExactBaseType(Class) methods, Incorrectly claimed allowIfSubTypeIsArray() is the only method missing in Jackson 2.x (actually allowSubTypesWithExplicitDeserializer() is missing), Incorrect version numbers for behaviour change of allowIfSubTypeIsArray() (should be 3.1.4 and 2.18.8, not 2.13/2.14/3.0), Incorrectly stated behaviour change timeline and details for allowIfSubTypeIsArray()], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer lists many methods that are not in the reference, omits several that are, and gets the missing‑in‑2.x method and version changes wrong, making it factually inconsistent and largely incomplete.
- jev: FAIL 0.10 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, grounded.value=0.01, completeness.value=0.18, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "Some of it" (0.55), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8]
- no_invented_names: invented names: [allowIfBaseTypeIsArray]

## jackson3-ptv / web-brave

```
**Public methods of `BasicPolymorphicTypeValidator.Builder` in the current Jackson 3 line (3.1.3 – the latest GA as of 2026‑10‑02)**  

| Method (fluent) | Return type | Short description |
|-----------------|-------------|-------------------|
| `allowIfBaseType(Class<?> baseType)` | Builder | Accept any sub‑type of the given *base* class (or its subclasses). |
| `allowIfBaseType(Pattern basePattern)` | Builder | Same as above, matched by a regular‑expression pattern. |
| `allowIfBaseTypeIsArray()` | Builder | Accept array types **when the array’s element type is also allowed**. |
| `allowIfBaseTypeIsCollection()` | Builder | Accept `java.util.Collection` sub‑types (e.g. `List`, `Set`). |
| `allowIfBaseTypeIsMap()` | Builder | Accept `java.util.Map` sub‑types. |
| `allowIfSubType(Class<?> subType)` | Builder | Accept the specific class (or any subclass) regardless of the declared base type. |
| `allowIfSubType(Pattern subPattern)` | Builder | Same as above, matched by a regular‑expression pattern. |
| `allowIfSubTypeIsArray()` | Builder | Accept any **array** type; the component type is validated against the configured allow‑list (changed in 3.1.3 – see below). |
| `denyForExactBaseType(Class<?> exactBase)` | Builder | Reject a *base* type exactly (no sub‑types). |
| `denyForExactBaseType(Pattern basePattern)` | Builder | Same as above, matched by a pattern. |
| `denyForExactSubType(Class<?> exactSub)` | Builder | Reject a specific *sub‑type* (no subclasses). |
| `denyForExactSubType(Pattern subPattern)` | Builder | Same as above, matched by a pattern. |
| `build()` | `BasicPolymorphicTypeValidator` | Create the immutable validator instance. |

*(All methods are `public` and return the builder itself (except `build`).)*  

---

### Methods **not present** in the latest Jackson 2 line (Jackson 2.13.x)

| Method | Reason it is new |
|--------|------------------|
| `allowIfBaseTypeIsArray()` | Introduced in Jackson 3 to give explicit control over array‑type bases; the older 2.x builder only had the generic `allowIfBaseType(Class<?>)` and `allowIfSubTypeIsArray()`. |
| `allowIfBaseTypeIsCollection()` | Added in Jackson 3 to let callers whitelist any `Collection`‑based base type without enumerating each concrete class. |
| `allowIfBaseTypeIsMap()` | Added in Jackson 3 for the same reason as the collection variant. |

All other builder methods (including `allowIfSubTypeIsArray()`) already existed in Jackson 2.13.x.

---

### Behaviour change of `allowIfSubTypeIsArray()`

| Version | Behaviour |
|---------|-----------|
| **Jackson 2.x – up to 2.13.x** | `allowIfSubTypeIsArray()` *unconditionally* allowed any array type (`clazz.isArray()`), **without** checking the array’s component (element) type against the allow‑list. This allowed, for example, `String[]` even when only `String` was permitted. |
| **Jackson 3.0.0 – 3.1.2** | Same “any‑array” behaviour as the 2.x line. |
| **Jackson 3.1.3 (released 01‑May‑2026)** | **Fixed** – the method now validates the array’s element type against the configured allow‑list. An array is allowed **only if** its component type is also allowed (or the component type matches an explicit `allowIfSubType` rule). This change was made to close CVE‑2026‑54513 (array‑subtype allow‑list bypass). |

*The change is recorded in the Jackson issue/commit:*  
- **Issue #5981** – “`BasicPolymorphicTypeValidator` setting `allowIfSubTypeIsArray()` should validate element type” (fixed in 3.1.3).  
- **Security advisory GHSA‑rmj7‑2vxq‑3g9f** (CVE‑2026‑54513) documents the vulnerability that motivated the fix.

---

## Sources
- **Jackson 2.13.3 Javadoc – Builder methods** – shows the full public API for the 2.x builder, including `allowIfSubTypeIsArray()` but not the three “IsArray/IsCollection/IsMap” base‑type helpers.  
  - [BasicPolymorphicTypeValidator.Builder (jackson‑databind 2.13.3)](https://javadoc.io/static/com.fasterxml.jackson.core/jackson-databind/2.13.3/com/fasterxml/jackson/databind/jsontype/BasicPolymorphicTypeValidator.Builder.html)  

- **Jackson 3.1.3 source code (Builder inner class)** – contains the new `allowIfBaseTypeIsArray()`, `allowIfBaseTypeIsCollection()`, and `allowIfBaseTypeIsMap()` methods and the updated `allowIfSubTypeIsArray()` implementation.  
  - [GitHub commit 24529da (Fix #5981) – BasicPolymorphicTypeValidator.java (3.1.3)](https://github.com/FasterXML/jackson-databind/commit/24529da29fdf46ff94ca38de9ebf31cd188f5e8e)  

- **Issue #5981 – Behaviour change of `allowIfSubTypeIsArray()`** (details of the fix and version).  
  - [GitHub issue #5981](https://github.com/FasterXML/jackson-databind/issues/5981)  

- **Security advisory GHSA‑rmj7‑2vxq‑3g9f (CVE‑2026‑54513)** – describes the array‑subtype allow‑list bypass that the 3.1.3 fix addresses.  
  - [GitHub advisory GHSA‑rmj7‑2vxq‑3g9f](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-rmj7-2vxq-3g9f)  

- **Jackson 3 release information** – confirms 3.1.3 as the latest GA version (May 2026).  
  - [Jackson‑databind 3.1.3 on Maven Central](https://search.maven.org/artifact/tools.jackson.core/jackson-databind/3.1.3/jar)
```

- checks: facts 5/10 missing=[Jackson 3 version 3.2.3, Jackson 2 version 2.22.3, allowSubTypesWithExplicitDeserializer, change version 3.1.4, change version 2.18.8] hallucinated=[allowIfBaseTypeIsArray, allowIfBaseTypeIsCollection, allowIfBaseTypeIsMap, denyForExactSubType]
- llm: FAIL 0.08 {factually_consistent=false, completeness=1, hallucinations=[Invented methods: allowIfBaseTypeIsArray(), allowIfBaseTypeIsCollection(), allowIfBaseTypeIsMap(), denyForExactSubType() and its overloads, denyForExactBaseType(Pattern), Incorrect version numbers: claims latest GA is 3.1.3 (reference says 3.2.3) and mentions Jackson 2.13.x instead of 2.22.3, Incorrect behaviour change version: states change in 3.1.3, reference says 3.1.4 (and 2.18.8), Incorrect list of overloads for allowIfBaseType and allowIfSubType (missing overloads with String, Pattern, TypeMatcher), Incorrectly states allowSubTypesWithExplicitDeserializer() is missing in 2.x (reference says it is missing, but assistant omits it entirely), Added security advisory and issue references not present in reference answer], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant's answer lists many methods that do not exist according to the reference, uses wrong version numbers, and misstates the behaviour change details, making it factually inaccurate and incomplete.
- jev: FAIL 0.17 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, grounded.value=0.01, completeness.value=0.32, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "Some of it" (0.96), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [Jackson 3 version 3.2.3, Jackson 2 version 2.22.3, allowSubTypesWithExplicitDeserializer, change version 3.1.4, change version 2.18.8]
- no_invented_names: invented names: [allowIfBaseTypeIsArray, allowIfBaseTypeIsCollection, allowIfBaseTypeIsMap, denyForExactSubType]

## jackson3-ptv / mcp

```
**BasicPolymorphicTypeValidator.Builder – public API (Jackson 3.x)**  

| Method (return type – parameters) | Present in Jackson 2.x? |
|-----------------------------------|------------------------|
| `BasicPolymorphicTypeValidator.Builder allowIfBaseType(Class<?> baseOfBase)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfBaseType(Pattern patternForBase)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfBaseType(String prefixForBase)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfBaseType(BasicPolymorphicTypeValidator.TypeMatcher matcher)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder denyForExactBaseType(Class<?> baseTypeToDeny)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfSubType(Class<?> subTypeBase)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfSubType(Pattern patternForSubType)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfSubType(String prefixForSubType)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfSubType(BasicPolymorphicTypeValidator.TypeMatcher matcher)` | ✔ |
| `BasicPolymorphicTypeValidator.Builder allowIfSubTypeIsArray()` | ✔ |
| `BasicPolymorphicTypeValidator build()` | ✔ |
| (protected helper methods – `_appendBaseMatcher`, `_appendSubClassMatcher`, `_appendSubNameMatcher`, `_appendSubClassMatcher`) | ✔ |

**Result:** The Jackson 3.x release does **not** introduce any new public methods on `BasicPolymorphicTypeValidator.Builder` – the full set is identical to the latest Jackson 2.x (2.22.3).

---

### Behaviour of `allowIfSubTypeIsArray()`

| Version | Behaviour |
|---------|------------|
| **≤ 2.18.7** | Calling `allowIfSubTypeIsArray()` made *any* array type automatically pass the validator. The validator would stop at the array class itself and **not** descend to the element type, so an attacker could wrap a disallowed class in an array (e.g. `Evil[]`) and bypass the allow‑list. |
| **2.18.8** (first version with the change) | The method was altered to *unwrap* arrays (recursively) and validate the **innermost element type** against the configured sub‑class/name matchers. Arrays of primitive, abstract, or interface element types are still accepted because they cannot instantiate a concrete gadget, but for concrete element types the validator now checks the element’s own allow‑list entry. |
| **2.19 + (and all later 2.x releases)** | Behaviour unchanged – the stricter validation introduced in 2.18.8 remains. |
| **Jackson 3.x** | Inherits the 2.18.8+ semantics; no further change has been made. |

**Summary of the change**

* **What changed?** The validator stopped treating “any array = allowed” as a shortcut. Instead it recursively inspects the array’s component type and applies the sub‑type matchers to that type.
* **Why?** To close the security issue [#5981] where an attacker could bypass an explicit sub‑type allow‑list by wrapping the prohibited class in an array.
* **When?** Implemented in **Jackson 2.18.8** and carried forward unchanged into the Jackson 3 line.

---

### Take‑away for developers

* If you previously relied on `allowIfSubTypeIsArray()` to “accept all arrays”, you now need to also list the element types you want to allow (or keep the old lax behaviour by custom matcher).  
* No additional Builder methods have been added in Jackson 3; existing fluent configuration works unchanged.
```

- checks: facts 6/10 missing=[Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, change version 3.1.4]
- llm: FAIL 0.50 {factually_consistent=false, completeness=2, hallucinations=[], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer omits the new public method allowSubTypesWithExplicitDeserializer() and incorrectly states that no new methods exist in Jackson 3, and it misses the version 3.1.4 where the allowIfSubTypeIsArray() behavior changed.
- jev: FAIL 0.23 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.02, completeness.value=0.43, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.02, needs at least 0.70)
- completeness: rated "Some of it" (1.30), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, change version 3.1.4]

## jackson3-ptv / mcp-toolsearch

```
**Jackson 3 (`tools.jackson.core:jackson‑databind:3.2.3`) – public methods of  
`BasicPolymorphicTypeValidator.Builder`**

| # | Method (public) | Signature |
|---|-----------------|-----------|
| 1 | `allowIfBaseType` | `Builder allowIfBaseType(Class<?> baseOfBase)` |
| 2 | `allowIfBaseType` | `Builder allowIfBaseType(Pattern patternForBase)` |
| 3 | `allowIfBaseType` | `Builder allowIfBaseType(String prefixForBase)` |
| 4 | `allowIfBaseType` | `Builder allowIfBaseType(BasicPolymorphicTypeValidator.TypeMatcher matcher)` |
| 5 | `allowIfSubType` | `Builder allowIfSubType(Class<?> subTypeBase)` |
| 6 | `allowIfSubType` | `Builder allowIfSubType(Pattern patternForSubType)` |
| 7 | `allowIfSubType` | `Builder allowIfSubType(String prefixForSubType)` |
| 8 | `allowIfSubType` | `Builder allowIfSubType(BasicPolymorphicTypeValidator.TypeMatcher matcher)` |
| 9 | `allowIfSubTypeIsArray` | `Builder allowIfSubTypeIsArray()` |
|10 | `allowSubTypesWithExplicitDeserializer` | `Builder allowSubTypesWithExplicitDeserializer()` |
|11 | `denyForExactBaseType` | `Builder denyForExactBaseType(Class<?> baseTypeToDeny)` |
|12 | `build` | `BasicPolymorphicTypeValidator build()` |

**Jackson 2 (`com.fasterxml.jackson.core:jackson‑databind:2.22.3`) – public methods of the same builder**

The same twelve methods are present in the 2.x release.  
**No method listed above is missing from Jackson 2 2.22.3.**  

---

### Behaviour change of `allowIfSubTypeIsArray()`

| Version where the change appeared | What changed |
|-----------------------------------|--------------|
| **Jackson 2.18.8** (2.x line) | Prior to 2.18.8 the method *implicitly allowed every array* – the validator returned **ALLOWED** for the array type without inspecting its component type. This allowed an attacker to wrap a disallowed class in an array (e.g. `Evil[]`) and bypass the class‑allow list. From 2.18.8 onward the method sets the internal flag `_acceptArrayTypes = true` **and** the validator now *unwraps arrays recursively* and validates the **innermost element type** against the configured sub‑class/name matchers. Thus only arrays whose element type is itself allowed will pass. |
| **Jackson 3.1.4** (3.x line) | The same security‑focused change is documented for the 3.x line (behaviour change in 3.1.4). The note clarifies that *earlier 3.x releases (≤ 3.0.x) behaved like the pre‑2.18.8 version* – they approved every array irrespective of element type. Starting with 3.1.4 the array‑validation mirrors the 2.18.8‑and‑later behaviour: arrays are unwrapped and the element type is checked. |

**Summary of the change**

* **Before the change**: `allowIfSubTypeIsArray()` caused the validator to treat any array type as “allowed”, regardless of what the array’s component class was.
* **After the change (≥ 2.18.8 / ≥ 3.1.4)**: The validator first unwraps the array (recursively for multi‑dimensional arrays) and then validates the **component type** against the configured sub‑type matchers. Primitive‑type arrays are always allowed (they cannot host gadget chains); for reference‑type arrays the element class must itself be allowed.

This change was introduced to close CVE‑related security issues (databind#5981) that allowed an attacker to bypass class‑allow lists by using an array wrapper.
```

- checks: facts 10/10
- llm: FAIL 0.25 {factually_consistent=false, completeness=3, hallucinations=[Claims that allowSubTypesWithExplicitDeserializer() exists in Jackson 2.22.3 (it does not), States that no method is missing from Jackson 2, contradicting the reference], trap_handled=null, semantic_pass=false, code_checks_pass=true} The answer lists the correct methods and version changes, but incorrectly asserts that all twelve methods exist in Jackson 2.x, missing the fact that allowSubTypesWithExplicitDeserializer() is absent.
- jev: FAIL 0.32 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.03, completeness.value=0.60, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.03, needs at least 0.70)

## jackson3-ptv / mcp-toolsearch-vector

```
**What exists today ( 2026‑10‑02 )**  

- The artifact **`com.fasterxml.jackson.core:jackson‑databind`** is still at version **`2.22.3`** (the latest released version).  
- Maven Central does **not** contain any 3.x release (including pre‑releases such as `3.0.0‑M1`, `3.0.0‑RC1`, etc.). A search for “jackson‑databind 3” or an attempt to list the Javadoc for a 3.x version returns a *NotFoundError*.  

**Therefore**

| Item | Availability |
|------|---------------|
| **Jackson 3 (`jackson‑databind` 3.x)** | **Not released** on Maven Central (no 3.x version, no pre‑release). |
| **`BasicPolymorphicTypeValidator.Builder` class** | Exists in the 2.x line (e.g., 2.22.3). |
| **Public methods of the Builder (2.22.3)** | `allowIfSubType(Class<?>)`, `allowIfSubType(Class<?>, PolymorphicTypeValidator.Validity)`, `allowIfSubType(String)`, `allowIfSubType(String, PolymorphicTypeValidator.Validity)`, `allowIfSubTypeIsArray()`, `allowIfBaseType(Class<?>)`, `allowIfBaseType(String)`, `allowIfBaseTypeIsArray()`, `allowIfBaseTypeMatches(Predicate<Class<?>>)`, `allowIfBaseTypeMatches(Predicate<String>)`, `allowIfSubTypeMatches(Predicate<Class<?>>)`, `allowIfSubTypeMatches(Predicate<String>)`, `build()` (and the inherited `toString()`, `hashCode()`, `equals(Object)`). |
| **Method that *does not* exist in a 3.x release** | Since a 3.x release does not exist, **no method can be said to be missing**. All the above methods are present in the current 2.x version. |
| **`allowIfSubTypeIsArray()` – behaviour change** | No behavioural change has been introduced because the 3.x line has not been released. In the current 2.x series (up to 2.22.3) `allowIfSubTypeIsArray()` simply configures the validator to accept any sub‑type that is an array type. There is no documented modification of this behaviour in any 2.x release, nor any 3.x release to compare against. |

**Bottom line** – as of today there is **no Jackson 3 (`jackson‑databind` 3.x) artifact** to compare against, so we cannot list “new” or “removed” builder methods, nor can we describe a change in the semantics of `allowIfSubTypeIsArray()`. All publicly documented builder methods belong to the 2.x line (e.g., 2.22.3). If a Jackson 3 release appears in the future, the same steps can be repeated: obtain the latest 3.x version via `get_latest_version`, list the `Builder` symbols with `list_javadoc_symbols`, and compare against the 2.x list.
```

- checks: facts 4/10 missing=[Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8] hallucinated=[allowIfBaseTypeIsArray, allowIfBaseTypeMatches, allowIfSubTypeMatches]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Stated that no Jackson 3 (jackson-databind 3.x) release exists on Maven Central, which is incorrect (3.2.3 exists)., Used the wrong Maven coordinates (com.fasterxml.jackson.core) for Jackson 3 instead of tools.jackson.core., Provided an incorrect list of public Builder methods for version 2.22.3, including methods that do not exist (e.g., allowIfBaseTypeIsArray, allowIfBaseTypeMatches, allowIfSubTypeMatches, overloads with PolymorphicTypeValidator.Validity)., Claimed that allowIfSubTypeIsArray() behavior has not changed, contrary to the documented change in 3.1.4 and 2.18.8., Asserted that no method is missing in a non‑existent 3.x release, ignoring the actual missing method allowSubTypesWithExplicitDeserializer() in the 2.x line.], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant's answer contradicts the verified reference on version existence, artifact coordinates, method list, missing method, and behavior change, making it factually incorrect and incomplete.
- jev: FAIL 0.06 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, grounded.value=0.02, completeness.value=0.10, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.02, needs at least 0.70)
- completeness: rated "None of it" (0.29), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8]
- no_invented_names: invented names: [allowIfBaseTypeIsArray, allowIfBaseTypeMatches, allowIfSubTypeMatches]

## spring-ai-hostedtool / base

```
**Answer**

In the most recent Spring AI release (the OpenAI module that ships with Spring AI 0.8.x), the sealed interface  

```java
org.springframework.ai.openai.api.tool.HostedTool
```  

is allowed to be **any one of three concrete types**:

| Concrete type | What it represents | When you’d typically use it |
|---------------|-------------------|----------------------------|
| **`HostedFunctionTool`** | A traditional OpenAI “function” tool – you give Spring AI a Java `Function` (or a POJO) together with a name, description and a generated JSON‑Schema. | When you want the library to generate the OpenAI‑compatible function definition for you. |
| **`HostedChatTool`** | A “chat‑completion” tool – the tool itself runs a (usually smaller) chat model and returns the result to the primary model. | When you need a multi‑step, LLM‑driven sub‑conversation (e.g., a “search” or “code‑generation” helper). |
| **`HostedRawTool`** (sometimes called **`RawTool`** in the source) | A **raw JSON** tool – you supply the exact JSON object that will be sent to the OpenAI API under the `tools` array. | When you want full control over the payload – for experimental features, custom‑type tools, or any definition that Spring AI does not yet have a typed wrapper for. |

> **Which one lets you send a tool definition as raw JSON?**  
> The **`HostedRawTool`** implementation is the one that allows you to provide a literal JSON definition. It bypasses the higher‑level helpers that automatically generate the JSON schema and lets you paste or construct the exact JSON object that OpenAI expects.  

---

### Quick code sketch

```java
import org.springframework.ai.openai.api.tool.*;

HostedTool rawTool = new HostedRawTool("""
{
  "type": "function",
  "function": {
    "name": "my_custom_tool",
    "description": "Does something obscure",
    "parameters": {
      "type": "object",
      "properties": {
        "value": { "type": "string" }
      },
      "required": ["value"]
    }
  }
}
""");

// Add to the request
ChatRequest request = ChatRequest.builder()
        .model("gpt-4o-mini")
        .messages(userMessage)
        .tools(List.of(rawTool))
        .build();
```

In contrast, a `HostedFunctionTool` would look like:

```java
HostedTool fnTool = new HostedFunctionTool(
        "my_custom_tool",
        "Does something obscure",
        Map.of("value", Map.of("type", "string")),
        List.of("value"));
```

Both are valid, but **only `HostedRawTool`** gives you the freedom to hand‑craft the JSON verbatim.
```

- checks: facts 1/7 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims the latest release is Spring AI 0.8.x (incorrect, reference says 2.1.0-M1 milestone), Uses package org.springframework.ai.openai.api.tool.HostedTool (incorrect, correct package is org.springframework.ai.openai.responses.HostedTool), States HostedTool permits only three concrete types (HostedFunctionTool, HostedChatTool, HostedRawTool) whereas reference lists six records: WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw, Invents classes HostedFunctionTool, HostedChatTool, HostedRawTool which do not exist in the referenced library, Describes a HostedRawTool that accepts a JSON string (incorrect; the Raw record takes a Map<String,Object> tool), Provides code examples using non‑existent APIs and classes], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant's answer contradicts the reference on version, package name, permitted types, and class names, providing multiple invented elements.
- jev: FAIL 0.12 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.01, completeness.value=0.24, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "Some of it" (0.71), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]

## spring-ai-hostedtool / shell

```
In the current **spring‑ai‑openai** release the sealed interface  

```java
public sealed interface HostedTool { … }
```  

permits the following concrete types (each defined as a `record` that implements the interface):

| HostedTool implementation | Purpose |
|---------------------------|---------|
| **WebSearch**             | OpenAI web‑search tool |
| **FileSearch**            | Vector‑store file‑search tool |
| **CodeInterpreter**       | sandboxed Python execution |
| **Mcp**                    | Remote MCP‑server tool calls |
| **ImageGeneration**       | Image‑generation tool |
| **Raw**                    | **Raw JSON** tool definition (escape‑hatch for tools not yet typed) |

**The `Raw` type** is the one that lets you provide a tool definition as raw JSON (e.g. `Map.of("type","local_shell")`). It converts that JSON into the OpenAI SDK `Tool` object at runtime.
```

- checks: facts 6/7 missing=[version 2.1.0-M1]
- llm: FAIL 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=false} The answer correctly lists all six HostedTool record types and identifies HostedTool.Raw as the one allowing raw JSON, matching the reference; no incorrect claims are made.
- jev: FAIL 0.53 {grounded=FAILED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.35, completeness.value=0.70, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.35, needs at least 0.70)
- required_facts: missing required facts: [version 2.1.0-M1]

## spring-ai-hostedtool / web-brave

```
**Permitted implementations of the sealed interface `HostedTool` (as of the latest Spring AI OpenAI release)**  

| Permit (class) | What it represents | How it is used |
|----------------|-------------------|----------------|
| `WebSearchTool` | The OpenAI‑hosted web‑search capability | `hostedTool().webSearch()` |
| `CodeInterpreterTool` | The OpenAI code‑interpreter (aka “Python”) tool | `hostedTool().codeInterpreter()` |
| `FileSearchTool` | The OpenAI file‑search tool (vector‑store lookup) | `hostedTool().fileSearch()` |
| `HostedMcpTool` | The “hosted MCP” tool that lets the model call a managed‑cloud‑provider endpoint | `hostedTool().hostedMcp()` |
| `ImageGenerationTool` | The OpenAI image‑generation (DALL‑E) tool | `hostedTool().imageGeneration()` |
| **`RawJsonTool`** | **Allows you to send a tool definition as raw JSON** – you supply the exact JSON payload that the OpenAI API expects, giving you full control over the tool schema and parameters. | `hostedTool().rawJson("{ … }")` |

**Which permit sends a raw JSON tool definition?**  
The `RawJsonTool` implementation lets you pass the tool description directly as a JSON string, bypassing the built‑in typed helpers. This is useful when you need to define a custom tool that isn’t covered by the other hosted‑tool helpers.  

*All of the above classes are the `permits` of the sealed interface `HostedTool` and are available through the `HostedTool` builder in the Spring AI OpenAI module.*  

**Sources**  
- Spring AI 2.1.0‑M1 release notes (September 2026) describing the new `HostedTool` API and its permits.  
- Spring AI OpenAI module source code for `HostedTool` (found in `spring-ai-openai/src/main/java/org/springframework/ai/openai/api/HostedTool.java`).
```

- checks: facts 7/7
- llm: FAIL 0.08 {factually_consistent=false, completeness=1, hallucinations=[WebSearchTool, CodeInterpreterTool, FileSearchTool, HostedMcpTool, ImageGenerationTool (as a separate class), RawJsonTool, hostedTool().webSearch(), hostedTool().codeInterpreter(), hostedTool().fileSearch(), hostedTool().hostedMcp(), hostedTool().imageGeneration(), hostedTool().rawJson(String)], trap_handled=null, semantic_pass=false, code_checks_pass=true} The assistant invents class and method names that do not exist in the reference (e.g., RawJsonTool instead of HostedTool.Raw) and misstates the API, making the answer factually inconsistent despite attempting to list six types.
- jev: FAIL 0.26 {grounded=FAILED, completeness=INCONCLUSIVE, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.04, completeness.value=0.48, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.04, needs at least 0.70)
The following checks were inconclusive and may need a clearer answer:
- completeness: the rubric did not settle whether this reaches 2.00 (0.58 of the probability supports the verdict, needs at least 0.60)

## spring-ai-hostedtool / mcp

```
I wasn’t able to locate a **`HostedTool`** type in the source or Javadoc of the current `spring‑ai‑openai` artifact (v 2.0.1 – the latest release on Maven Central at the time of writing). `HostedTool` isn’t listed among the compiled classes or source files in that module, and a search of the public API for that name returns no matches.

If `HostedTool` does exist in a newer Spring AI release (or in a different Spring AI artifact such as `spring‑ai‑core` or a later version of `spring‑ai‑openai`), the typical pattern for a sealed tool‑definition interface in Spring AI is:

| Permitted implementation (subtype) | What it does |
|------------------------------------|--------------|
| **`JavaMethodTool`** (or similarly named) | Wraps a regular Java method so the OpenAI function‑calling API can invoke it. |
| **`JsonTool`** (or a similarly named subtype) | Holds a raw JSON description of the tool (name, description, parameters, etc.) and sends that JSON directly to the OpenAI API. |

In that pattern the **`JsonTool`** (or its equivalent) is the subtype that lets you provide a tool definition as raw JSON.

Because I couldn’t verify the exact class names or the sealed‑interface declaration in the released artifact, I can’t confirm the precise names or whether additional sub‑types exist. If you are using a newer Spring AI version or a different module, you may need to check the Javadoc or source of that specific artifact for the exact `HostedTool` hierarchy.
```

- checks: facts 1/7 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims that HostedTool does not exist in the latest released artifact (v2.0.1) whereas it does exist in the milestone 2.1.0‑M1, Invents subtypes JavaMethodTool and JsonTool as permitted implementations of HostedTool, Suggests a pattern that does not match the actual six records (WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw), States that JsonTool (or equivalent) sends raw JSON, which is not the actual HostedTool.Raw record], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer provides incorrect information about the existence and contents of HostedTool and invents unrelated class names, failing to answer the question correctly.
- jev: FAIL 0.26 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.33, completeness.value=0.19, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.33, needs at least 0.70)
- completeness: rated "Some of it" (0.58), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]

## spring-ai-hostedtool / mcp-toolsearch

```
**Spring AI 2.0.1 – `org.springframework.ai.openai.HostedTool`**

The `HostedTool` type is a **sealed interface**. In the current (2.0.1) release it has three permitted sub‑types:

| Permitted type | What it represents |
|----------------|--------------------|
| `org.springframework.ai.openai.tool.FunctionDefinitionTool` | A tool expressed with Spring AI’s `FunctionDefinition` builder (name, description, JSON‑Schema parameters). |
| `org.springframework.ai.openai.tool.ChatTool` | A tool expressed with the newer OpenAI “chat‑tool” model (a `Tool` object built via the builder API). |
| `org.springframework.ai.openai.tool.RawJsonTool` (sometimes shown as `JsonTool`) | **Allows you to pass the complete OpenAI tool definition as a raw JSON string** – you supply the exact JSON that OpenAI expects, without any Spring‑AI conversion. |

> **Which type lets you send a tool definition as raw JSON?**  
> The **`RawJsonTool`** (also known as `JsonTool`) implementation of `HostedTool` is the one designed for that purpose. It accepts a pre‑built JSON payload and forwards it directly to the OpenAI API.
```

- checks: facts 1/7 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims the latest release is Spring AI 2.0.1; the reference says the latest milestone is 2.1.0-M1 and 2.0.1 does not contain HostedTool., States HostedTool permits three sub‑types (FunctionDefinitionTool, ChatTool, RawJsonTool/JsonTool) whereas the reference lists six records (WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw)., Introduces classes/interfaces (FunctionDefinitionTool, ChatTool, RawJsonTool/JsonTool) that are not present in the referenced library version., Suggests RawJsonTool is the raw‑JSON variant, while the correct type per reference is HostedTool.Raw.], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant provides entirely incorrect information about the version, the permitted types of the sealed interface, and the raw‑JSON implementation, contradicting the verified reference.
- jev: FAIL 0.05 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.01, completeness.value=0.09, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "None of it" (0.27), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]

## spring-ai-hostedtool / mcp-toolsearch-vector

```
IllegalStateException: Conversion from JSON to java.util.List<java.lang.String> failed <- StreamReadException: Unrecognized token 'Total': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')
 at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]
```

- checks: facts 0/7 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]
- llm: FAIL 0.33 {factually_consistent=false, completeness=0, hallucinations=[], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant gave no answer, so it does not provide the required information about the sealed interface or the Raw variant, resulting in a lack of factual consistency and 0 completeness.
- jev: FAIL 0.37 {grounded=PASSED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.72, completeness.value=0.02, semantic_pass=false} - completeness: rated "None of it" (0.05), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw]

## agentutils-shell-trap / base

```
Below is a **concise, up‑to‑date cheat‑sheet** for the two questions you asked.  
All snippets compile against the **latest releases** (as of Oct 2026):

| Library / version | Relevant class | Default behaviour | How to change it |
|-------------------|----------------|-------------------|------------------|
| **spring‑ai‑agent‑utils** `2.5.0` (or newer) | `LocalExecBackend` | Runs commands through **`/bin/sh -c`** on Linux (the same shell Java’s `ProcessBuilder` uses when you give it a *single* string). | Use the *builder* to supply a custom shell or set the Spring property `spring.ai.agent.local-exec.shell`. |
| **typesafe‑spring‑ai** `1.3.2` (or newer) | `JevJudge.Builder.rubric(..)` | – | Pass a `Path`, `File`, `Resource`, or a raw JSON‑String that describes your rubric. |

Below you’ll find **exact code, the property key, and the reasoning** behind each point.

---

## 1️⃣  `LocalExecBackend` – Which shell does it use, how to override it, and environment inheritance?

### 1.1 Default shell on Linux

`LocalExecBackend` internally creates a `ProcessBuilder` this way (simplified):

```java
// Inside LocalExecBackend.execute(String command)
String[] exec = { defaultShell(), "-c", command };
ProcessBuilder pb = new ProcessBuilder(exec);
```

```java
// defaultShell() implementation (Linux/Unix branch)
private static String defaultShell() {
    return System.getenv().getOrDefault("SHELL", "/bin/sh");
}
```

*Result:* on a typical Linux machine the JVM inherits the user’s `$SHELL` environment variable (`/bin/bash`, `/usr/bin/zsh`, …).  
If that variable is **not set**, the fallback is **`/bin/sh`**. In practice the vast majority of CI/CD containers and servers have `$SHELL` unset, so you’ll see `"/bin/sh -c"`.

> **TL;DR:** **`/bin/sh -c`** is the default (or `$SHELL` if it exists).

### 1.2 Overriding the shell

There are **two equivalent ways**:

| Method | Code | Spring property |
|--------|------|-----------------|
| **Builder API** (imperative) | ```java\nLocalExecBackend backend = LocalExecBackend.builder()\n        .shell("/bin/bash")           // any absolute path you like\n        .build();\n``` | N/A |
| **Spring Boot configuration** (declarative) | ```yaml\nspring:\n  ai:\n    agent:\n      local-exec:\n        shell: /bin/bash   # supports any executable that can take a \"-c\" argument\n``` | `spring.ai.agent.local-exec.shell` |

> **Why it works:** The builder stores the value in a private field `String shell = "/bin/sh"` (or `$SHELL`). The bean created by `LocalExecBackendAutoConfiguration` reads the same property and passes it to the builder, so both approaches converge on the same implementation.

### 1.3 Do child processes inherit the JVM’s environment?

**Yes – by default.**  

`ProcessBuilder` starts with **a copy of the current Java process’s environment** (`System.getenv()`). `LocalExecBackend` does not clear or replace that map. Consequently:

* All variables you set in the JVM (e.g. `System.setProperty`, `System.getenv` – via a wrapper like `EnvVar.set("FOO=bar")`) are visible to the spawned command.
* If you *do* want a clean environment you must explicitly clear or replace it:

```java
ProcessBuilder pb = new ProcessBuilder(shell, "-c", command);
pb.environment().clear();                     // start from scratch
pb.environment().put("PATH", "/usr/bin");     // add what you need
```

`LocalExecBackend` exposes this via the builder as well:

```java
LocalExecBackend backend = LocalExecBackend.builder()
        .inheritParentEnv(false)   // <-- disables the default copy
        .environment(Map.of("PATH", "/usr/bin")) // custom env for every exec
        .build();
```

> **Summary of the three questions**  

| Question | Answer |
|----------|--------|
| **Shell used by default on Linux?** | `"/bin/sh -c"` (or `$SHELL` if that env‑var is defined). |
| **How to override it?** | `LocalExecBackend.builder().shell("/bin/bash")` **or** set `spring.ai.agent.local-exec.shell` in `application.yml`/`properties`. |
| **Do child processes inherit the JVM’s environment?** | **Yes** – `ProcessBuilder` copies the JVM’s environment unless you disable it with `inheritParentEnv(false)` or manually edit `pb.environment()`. |

---

## 2️⃣  Using `JevJudge.Builder.rubric()` from **typesafe‑spring‑ai**

`JevJudge` is a **type‑safe, Spring‑friendly wrapper** around the “Jev” automatic‑grading engine. The **rubric** is a JSON document that tells Jev how to score a submission (test cases, weightings, thresholds, etc.). The builder method `rubric(...)` is overloaded to accept several convenient sources.

### 2️⃣.1 Available overloads (as of 1.3.2)

| Overload | Parameter type | Typical use‑case |
|----------|----------------|-----------------|
| `rubric(Path path)` | `java.nio.file.Path` | Load from a file on the classpath or filesystem. |
| `rubric(File file)` | `java.io.File` | Same as above, when you already have a `File`. |
| `rubric(Resource resource)` | `org.springframework.core.io.Resource` | Load from a Spring `Resource` (classpath, URL, etc.). |
| `rubric(String json)` | `String` | Inline JSON or the content you have already read. |
| `rubric(Consumer<RubricBuilder> cfg)` | `Consumer<RubricBuilder>` | Build the rubric programmatically with the fluent DSL that ships with `typesafe-spring-ai`. |

### 2️⃣.2 Practical examples

#### a) Load a rubric **from the classpath** (`src/main/resources/jevrubric.json`)

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.typesafe.spring.ai.jevj.JevJudge;

@Configuration
public class JevConfig {

    @Bean
    JevJudge jevJudge() {
        return JevJudge.builder()
                // Spring’s `Resource` abstraction – will resolve classpath:, file:, http: …
                .rubric(new org.springframework.core.io.ClassPathResource("jevrubric.json"))
                .build();
    }
}
```

*Why this works*: `ClassPathResource` implements `Resource`; `JevJudge.Builder.rubric(Resource)` reads the stream and parses the JSON.

#### b) Provide a **filesystem** path (useful for externalized rubrics)

```java
@Bean
JevJudge jevJudge(@Value("${jev.rubric.path}") Path rubricPath) {
    return JevJudge.builder()
            .rubric(rubricPath)   // Path overload
            .build();
}
```

`application.yml`

```yaml
jev:
  rubric:
    path: /etc/jev/rubric.json
```

#### c) Inline **JSON string** (good for tests or tiny rubrics)

```java
@Bean
JevJudge testJevJudge() {
    String json = """
        {
          "tests": [
            { "name": "HelloWorld", "cmd": "java Main", "expected": "Hello, world!" }
          ],
          "grading": { "passScore": 100, "failScore": 0 }
        }
        """;

    return JevJudge.builder()
            .rubric(json)   // String overload
            .build();
}
```

#### d) Build the rubric **programmatically** using the DSL

```java
import com.typesafe.spring.ai.jevj.rubric.RubricBuilder;

@Bean
JevJudge dynamicJevJudge() {
    return JevJudge.builder()
            .rubric(rb -> rb
                    .test("HelloWorld")
                        .command("java Main")
                        .expectedOutput("Hello, world!")
                        .weight(1.0)
                    .endTest()
                    .grading(g -> g
                        .passScore(100)
                        .failScore(0)
                    )
            )
            .build();
}
```

*The DSL* (`RubricBuilder`) mirrors the JSON structure but gives you compile‑time safety and auto‑completion.

### 2️⃣.3 Combining with **Spring Boot auto‑configuration**

`typesafe-spring-ai` ships an auto‑configuration class (`JevJudgeAutoConfiguration`) that will create a `JevJudge` bean **if** you provide the property:

```yaml
jev:
  rubric: classpath:jevrubric.json   # or file:/opt/jev/rubric.json
  # optional: extra env vars you want the judge to see
  env:
    PYTHONPATH: /opt/python/lib
```

Under the hood the auto‑config does:

```java
JevJudge.builder()
        .rubric(resourceFromProperty("jev.rubric"))
        .environment(envMapFromProperty("jev.env"))
        .build();
```

So you **don’t** need to write a `@Bean` unless you want customisation beyond the property file.

### 2️⃣.4 Full minimal Spring Boot example (all pieces together)

```java
// src/main/java/com/example/demo/DemoApplication.java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication   // enables JevJudgeAutoConfiguration automatically
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

// src/main/resources/application.yml
spring:
  ai:
    agent:
      local-exec:
        shell: /bin/bash          # <‑‑ (optional) override for LocalExecBackend
jev:
  rubric: classpath:jevrubric.json
  env:
    JAVA_HOME: /usr/lib/jvm/java-21-openjdk

// src/main/resources/jevrubric.json
{
  "tests": [
    {
      "name": "Factorial",
      "cmd": "java -cp . Factorial 5",
      "expected": "120"
    }
  ],
  "grading": {
    "passScore": 100,
    "failScore": 0
  }
}
```

Now you can inject `JevJudge` wherever you need it:

```java
@Service
public class GradingService {

    private final JevJudge jevJudge;

    public GradingService(JevJudge jevJudge) {
        this.jevJudge = jevJudge;
    }

    public GradingResult grade(String submissionDir) {
        // `submissionDir` contains the student's source files, compiled already etc.
        return jevJudge.evaluate(Path.of(submissionDir));
    }
}
```

---

## TL;DR Quick Reference

| Feature | Default | How to change |
|---------|---------|---------------|
| **Shell used by `LocalExecBackend` on Linux** | `/bin/sh -c` (or `$SHELL` if defined) | `LocalExecBackend.builder().shell("/bin/bash")` **or** `spring.ai.agent.local-exec.shell=/bin/bash` |
| **Do spawned processes inherit the JVM environment?** | **Yes** – `ProcessBuilder` copies `System.getenv()` by default | `builder().inheritParentEnv(false)` to disable; otherwise edit `builder().environment(map)` to add/override variables. |
| **Load a rubric with JevJudge** | N/A | `JevJudge.builder().rubric(Path|File|Resource|String|Consumer<RubricBuilder>)`  <br>or use the auto‑configuration property `jev.rubric=classpath:…` |
| **Typical Spring‑Boot injection** | `@Autowired JevJudge jevJudge;` | Nothing extra needed if you set `jev.rubric` property. |

Feel free to ask if you need a deeper dive into any of the overloads or a concrete integration with a specific agent workflow!
```

- checks: facts 1/5 missing=[version 0.12.0, shellCommand, cleanEnvironment, says rubric() does not exist] hallucinated=[JevConfig, JevJudgeAutoConfiguration]
- llm: FAIL 0.13 {factually_consistent=false, completeness=2, hallucinations=[Claims the default shell is "/bin/sh -c" (reference says "/bin/bash -c"), Mentions a builder method .shell(...) (reference uses .shellCommand(String...)), Describes .inheritParentEnv(false) and environment handling not present in reference, States that JevJudge.Builder has a rubric(...) method with multiple overloads (reference says no such method exists), Provides property key spring.ai.agent.local-exec.shell which is not documented in reference], trap_handled=false, semantic_pass=false, code_checks_pass=false} The answer contains several incorrect details about the default shell, override API, environment handling, and incorrectly asserts the existence of a rubric() method, contradicting the reference.
- jev: FAIL 0.06 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, trap_handled=FAILED, grounded.value=0.01, completeness.value=0.16, trap_handled.value=0.02, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "None of it" (0.48), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 0.12.0, shellCommand, cleanEnvironment, says rubric() does not exist]
- no_invented_names: invented names: [JevConfig, JevJudgeAutoConfiguration]
- trap_handled: The answer describes or invents the non-existent API (scored 0.02, needs at least 0.70)

## agentutils-shell-trap / shell

```
**1. `LocalExecBackend` – the shell that is used, how to change it and what environment the launched process sees**

| Question | Answer (as of the latest `spring‑ai‑agent‑utils` release) |
|----------|-----------------------------------------------------------|
| **What shell is used on Linux by default?** | The backend builds a `ProcessBuilder` that wraps the command in a *POSIX‑compatible* shell.  The source code calls `new ProcessBuilder("/bin/sh", "-c", command)` (the classic “/bin/sh” entry point).  In practice this ends up using whatever `/bin/sh` points at on the host – on typical Linux distributions that is **`/bin/bash`** (or `dash` on Debian‑derived systems). |
| **How can the default shell be overridden?** | `LocalExecBackend` exposes a builder method/constructor argument called **`shell`** (or, when the bean is created from Spring properties, a property named `spring.ai.agent.exec.shell`).  Supplying a different executable (e.g., `"/usr/bin/zsh"` or `"/bin/bash"` explicitly) will replace the default.  Example (Java DSL):  <br>```java<br>LocalExecBackend backend = LocalExecBackend.builder()<br>    .shell("/usr/bin/zsh")   // ← override<br>    .build();<br>```<br>When the bean is auto‑configured you can set the property in *application.yml*:<br>```yaml<br>spring:<br>  ai:<br>    agent:<br>      exec:<br>        shell: /usr/bin/zsh<br>``` |
| **Do child processes inherit the JVM’s environment by default?** | Yes.  `LocalExecBackend` creates the `ProcessBuilder` with **`inheritIO()`** and **does not clear the environment map**.  Consequently the new process receives a copy of the JVM’s environment (`System.getenv()`) unless the caller explicitly modifies the builder’s environment.  This is the same behaviour as any ordinary `new ProcessBuilder(...).start()`. |

*What could not be verified directly* – The exact name of the Spring property (`spring.ai.agent.exec.shell`) and the exact builder method (`shell(String)`) were inferred from the source pattern used in the same project (the public `LocalExecBackend` class lives in the `spring‑ai‑agent‑utils` repository; the source could not be printed here, but the constructor signature and the property name are consistent with the other back‑ends in the code base).

---

**2. `JevJudge.Builder.rubric()` – how to use it from *typesafe‑spring‑ai***

`JevJudge` is the “Java Evaluation Judge” that ships with the **`typesafe-spring-ai`** module.  Its purpose is to let you describe a *rubric* (a set of weighted test criteria) and then run a judgment on an AI‑generated answer, producing a numeric score and a detailed report.

The static entry point is `JevJudge.builder()`.  The builder exposes a method `rubric(Rubric rubric)` (or an overloaded version that takes a *`Consumer<Rubric.Builder>`* to let you build the rubric inline).  The typical usage pattern looks like this:

```java
import org.springframework.ai.typesafe.JevJudge;
import org.springframework.ai.typesafe.Rubric;

// 1️⃣  Build the rubric (what you want to check)
Rubric rubric = Rubric.builder()
        .criterion("Correctness",   0.5)   // weight 50 %
        .criterion("Completeness", 0.3)   // weight 30 %
        .criterion("Style",        0.2)   // weight 20 %
        .build();

// 2️⃣  Create the judge, supplying the rubric
JevJudge judge = JevJudge.builder()
        .rubric(rubric)               // ← the method you asked about
        .build();

// 3️⃣  Run the judgement
String aiAnswer = "...";               // whatever the model returned
JevResult result = judge.judge(aiAnswer);

// 4️⃣  Inspect the result
System.out.println("Overall score: " + result.overallScore());
result.details().forEach(System.out::println);
```

**Inlining the rubric** (no separate `Rubric` object) is also supported:

```java
JevJudge judge = JevJudge.builder()
        .rubric(b -> b
                .criterion("Correctness",   0.5)
                .criterion("Completeness", 0.3)
                .criterion("Style",        0.2))
        .build();
```

*What could not be verified directly* – The exact import package (`org.springframework.ai.typesafe.*`) and the exact method signatures were taken from the published Javadoc for the `typesafe-spring-ai` artifact (the source is not in the local checkout).  The semantics of the `rubric` method (accepting either a ready‑made `Rubric` or a lambda that builds one) are confirmed by the Javadoc and by the unit‑test examples that live in the `typesafe-spring-ai` test sources.

---

### TL;DR

* **Linux default shell:** `/bin/sh` (which on most distros resolves to `/bin/bash`); override with the `shell` builder option or the `spring.ai.agent.exec.shell` property.  
* **Environment inheritance:** child processes inherit the JVM’s environment unless you explicitly change the `ProcessBuilder` environment.  
* **Using `JevJudge.Builder.rubric()`:** pass a `Rubric` instance (or a lambda that builds one) to the builder; then build the judge and call `judge.judge(yourAnswer)` to get a scored result.  

If you need the exact source lines for the `LocalExecBackend` implementation or the full Javadoc for `JevJudge`, they can be looked up in the respective GitHub repositories (`spring-ai-community/spring-ai-agent-utils` and `spring-ai-community/typesafe-spring-ai`).  The information above reflects the behavior observed in the latest released version.
```

- checks: facts 1/5 missing=[version 0.12.0, shellCommand, cleanEnvironment, says rubric() does not exist] hallucinated=[JevResult]
- llm: FAIL 0.13 {factually_consistent=false, completeness=2, hallucinations=[Claims the default shell on Linux is "/bin/sh" (or resolves to "/bin/bash") instead of "/bin/bash -c" as per the reference, States the override method/property is "shell" or "spring.ai.agent.exec.shell" rather than the correct builder().shellCommand(String... ), Describes a JevJudge.Builder.rubric() method that does not exist], trap_handled=false, semantic_pass=false, code_checks_pass=false} The answer covers all parts of the question but provides several inaccurate details that contradict the reference, including the default shell command, the override API, and the existence of a rubric() method.
- jev: FAIL 0.09 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, trap_handled=FAILED, grounded.value=0.01, completeness.value=0.24, trap_handled.value=0.02, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "Some of it" (0.71), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 0.12.0, shellCommand, cleanEnvironment, says rubric() does not exist]
- no_invented_names: invented names: [JevResult]
- trap_handled: The answer describes or invents the non-existent API (scored 0.02, needs at least 0.70)

## agentutils-shell-trap / web-brave

```
**`LocalExecBackend` (spring‑ai‑agent‑utils)**  

* **Default shell on Linux** – If no shell is supplied, the backend builds the command list as  

```java
List<String> shell = os.contains("win")
        ? List.of("cmd.exe", "/c")
        : List.of("/bin/bash", "-c");   // ← default on Linux/macOS
```

* **How to override it** – The builder exposes a `shellCommand(String… shellCommand)` method.  
  Example:

```java
LocalExecBackend backend = LocalExecBackend.builder()
        .shellCommand("/usr/bin/zsh", "-c")   // any executable + arguments
        .build();
```

* **Environment inheritance** – By default the child process inherits the JVM’s environment.  
  In `launch(ExecSpec spec)` the code does:

```java
if (this.cleanEnvironment) {
    processBuilder.environment().clear();   // only when cleanEnvironment is true
}
processBuilder.environment().putAll(this.environment);
processBuilder.environment().putAll(spec.env());
```

Since `cleanEnvironment` defaults to `false`, the existing JVM environment is kept and then the backend‑level and per‑spec variables are added on top of it.

**Source** – `LocalExecBackend.java` (see the default‑shell selection, `shellCommand` builder method, and the environment handling in `launch`)【WebFetch】.

---

**`JevJudge.Builder.rubric()` (typesafe‑spring‑ai)**  

`JevJudge` is built through a fluent builder. The `rubric()` method lets you attach a *rubric* – a reusable collection of criteria (scores, yes‑no‑or‑unknown (Noul) checks, etc.) – instead of adding each criterion individually.

Typical usage:

```java
// 1. Create a rubric (it can be reused for many judges)
JevRubric rubric = JevRubric.builder()
        .score("helpfulness",
                Score.builder()
                        .instructions("How well does `assistant_answer` address `user_question`?")
                        .level("Terrible: irrelevant or off‑topic")
                        .level("Mostly unhelpful: misses the main point")
                        .level("Mostly helpful: minor gaps")
                        .level("Excellent: fully and correctly addressed")
                        .build(),
                2.0)                         // weight
        .noul("is_plausible",
                Noul.builder()
                        .instructions("Are the values in `assistant_answer` physically plausible?")
                        .whenFalse("Contains an impossible or absurd value")
                        .build(),
                0.7)                         // threshold
        .build();

// 2. Build the judge, passing the TypeSafe client and the rubric
JevJudge judge = JevJudge.builder(typeSafeClient)
        .rubric(rubric)                 // <-- the method you asked about
        .build();
```

* **What the method does** – `Builder.rubric(JevRubric rubric)` copies every criterion contained in the supplied `JevRubric` into the builder’s internal list. It is equivalent to calling the individual `score(...)`, `noul(...)`, `check(...)`, etc., for each entry in the rubric, but lets you define the set of criteria once and reuse it across many judges.

* **Alternative fluent form** – The method also has an overload that accepts a `Consumer<JevRubric.Builder>` so you can define the rubric inline:

```java
JevJudge judge = JevJudge.builder(typeSafeClient)
        .rubric(r -> r
                .score("helpfulness", helpScore, 2.0)
                .noul("is_plausible", plausibilityNoul, 0.7))
        .build();
```

**Source** – `JevJudge.java` (the `Builder` inner class defines `rubric(JevRubric)` and the overload taking a `Consumer<JevRubric.Builder>`)【WebFetch】.
```

- checks: facts 3/5 missing=[version 0.12.0, says rubric() does not exist] hallucinated=[JevRubric]
- llm: FAIL 0.19 {factually_consistent=false, completeness=3, hallucinations=[Claims that JevJudge.Builder has a rubric() method and overloads, which do not exist in typesafe‑spring‑ai 0.3.0], trap_handled=false, semantic_pass=false, code_checks_pass=false} The answer gets the LocalExecBackend details correct, but invents a non‑existent rubric() API for JevJudge.Builder, contradicting the reference.
- jev: FAIL 0.16 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, trap_handled=FAILED, grounded.value=0.02, completeness.value=0.45, trap_handled.value=0.02, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.02, needs at least 0.70)
- completeness: rated "Some of it" (1.35), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 0.12.0, says rubric() does not exist]
- no_invented_names: invented names: [JevRubric]
- trap_handled: The answer describes or invents the non-existent API (scored 0.02, needs at least 0.70)

## agentutils-shell-trap / mcp

```
**1. `LocalExecBackend` (spring‑ai‑agent‑utils 0.12.0)**  

| Item | What the Javadoc shows |
|------|------------------------|
| **Default shell on Linux** | The builder’s *shellCommand* defaults to **`/bin/bash -c`** (the Javadoc states: “Default: `cmd.exe /c` on Windows, **`/bin/bash -c` elsewhere**.”) |
| **How to override it** | Call the builder method **`shellCommand(String… shellCommand)`** and pass the command‑array you want. Example: <br/>```java<br/>LocalExecBackend backend = LocalExecBackend.builder()<br/>        .shellCommand("/usr/bin/sh", "-c")   // your own shell<br/>        .build();<br/>``` |
| **Do child processes inherit the JVM’s environment by default?** | Yes. The builder has a **`cleanEnvironment(boolean cleanEnvironment)`** flag whose default is **`false`**, meaning the spawned process inherits the full JVM environment (including any secrets). Setting `cleanEnvironment(true)` makes the child start with an empty environment. |

---

**2. `JevJudge.Builder.rubric()` (typesafe‑spring‑ai 0.3.0)**  

The Javadoc for `org.springaicommunity.typesafe.judge.JevJudge.Builder` lists the following builder methods: `check`, `choice`, `criterion`, `failFast`, `failOnError`, `failOnInconclusive`, `feedbackRenderer`, `minConfidence`, `noul`, `score`, and `build`. A method named **`rubric()`** does **not appear** in the generated API documentation or in the source files that were listed for the artifact.

Because the symbol cannot be found in the published Javadoc or source, I cannot verify its existence or its usage. If a `rubric()` method does exist in a newer (or unreleased) version, you would typically call it on the builder the same way the other configuration methods are used, e.g.:

```java
JevJudge judge = JevJudge.builder(typeSafeClient)
        .rubric(myRubric)   // <-- hypothetical call
        .score("helpfulness", scoreQuestion, 2.0)
        .build();
```

But with the current released version (0.3.0) there is **no `rubric()` method** to invoke. You can build a judge by adding **criteria** (`score`, `noul`, `choice`, …) and optional **code checks** (`check`) as shown in the Javadoc examples. If you need a “rubric”‑style judgment you would either:

* compose the desired criteria manually with the builder methods shown above, or  
* upgrade to a version where a `rubric(...)` method is added (if such a feature is planned).

---

### Summary of what could be verified

* **Default Linux shell** – `/bin/bash -c` (verified).  
* **Overriding the shell** – use `builder.shellCommand(String…)` (verified).  
* **Environment inheritance** – defaults to inheriting the JVM environment; can be disabled with `cleanEnvironment(true)` (verified).  
* **`JevJudge.Builder.rubric()`** – not present in the current `0.3.0` release; I could not locate or verify such a method. If you need a rubric‑based judge, you’ll have to construct it with the existing builder methods or check a newer library version.
```

- checks: facts 5/5
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=true, semantic_pass=true, code_checks_pass=true} The answer correctly states the default shell, the override method, environment inheritance, and that JevJudge.Builder has no rubric() method, matching the reference.
- jev: PASS 0.78 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED, grounded.value=0.70, completeness.value=0.80, trap_handled.value=0.84, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED]

## agentutils-shell-trap / mcp-toolsearch

```
**spring‑ai‑agent‑utils (latest 0.12.0)**  

| Item | Answer |
|------|--------|
| **Default shell on Linux** | `LocalExecBackend` uses **`/bin/bash -c`** as the command‑line interpreter on non‑Windows platforms. (Windows defaults to `cmd.exe /c`.) |
| **How to override** | Call the builder’s **`shellCommand(String… shellCommand)`** method. Example:  <br>`LocalExecBackend backend = new LocalExecBackend.Builder()<br>  .shellCommand("/usr/bin/zsh", "-c")<br>  .build();` |
| **Environment inheritance** | By default child processes **inherit the JVM’s environment**. The builder option `cleanEnvironment(true)` can be used to start a process with an empty environment; its default is `false` (i.e. inherit). |

**typesafe‑spring‑ai (latest 0.3.0)** – `JevJudge.Builder.rubric()`  

The Javadoc for `org.springaicommunity.typesafe.judge.JevJudge.Builder` in version 0.3.0 does **not** contain a `rubric()` method (the listed methods are `build()`, `check(...)`, `choice(...)`, `criterion(...)`, `failFast(...)`, `failOnError(...)`, `failOnInconclusive(...)`, `feedbackRenderer(...)`, `minConfidence(...)`, `noul(...)`, `score(...)`).  

Therefore, as of the current release there is **no `rubric()` API** on `JevJudge.Builder`. If you need rubric‑style functionality you will have to:

1. **Check a different version** of *typesafe‑spring‑ai* (e.g., a newer milestone or snapshot) where such a method might have been added, or  
2. **Implement it yourself** by using the existing builder methods to compose the desired checks (e.g., `criterion(...)`, `choice(...)`, `score(...)`, `noul(...)`) and then invoking `build()` to obtain a `JevJudge` instance.

If you discover a version that does expose `rubric()`, the typical usage pattern would mirror other builder methods, e.g.:

```java
JevJudge judge = new JevJudge.Builder()
        .rubric(myRubric)   // hypothetical method
        .build();
```

But with the current released artifact (0.3.0) such a method is absent.
```

- checks: facts 5/5
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=true, semantic_pass=true, code_checks_pass=true} The answer correctly states the default shell, how to override it, the environment inheritance behavior, and accurately notes that JevJudge.Builder has no rubric() method, matching the reference.
- jev: FAIL 0.81 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED, grounded.value=0.67, completeness.value=0.81, trap_handled.value=0.94, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.67, needs at least 0.70)

## agentutils-shell-trap / mcp-toolsearch-vector

```

```

- checks: facts 0/5 missing=[version 0.12.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- llm: FAIL 0.50 {factually_consistent=true, completeness=0, hallucinations=[], trap_handled=false, semantic_pass=false, code_checks_pass=false} The assistant provided no answer, so it does not contain any contradictory statements (factually consistent) but fails to address any part of the question, yielding zero completeness and does not handle the trap about the non‑existent rubric() method.
- jev: FAIL 0.25 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, trap_handled=FAILED, grounded.value=0.61, completeness.value=0.01, trap_handled.value=0.12, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.61, needs at least 0.70)
- completeness: rated "None of it" (0.03), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 0.12.0, default shell /bin/bash, shellCommand, cleanEnvironment, says rubric() does not exist]
- trap_handled: The answer describes or invents the non-existent API (scored 0.12, needs at least 0.70)
