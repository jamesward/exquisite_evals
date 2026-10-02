# Eval results

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp-toolsearch-vector | 15/15 | PASS 1.00 | PASS 0.93 | 31703 | 1751 | 907 | 8 | 5 | 20.9 |
| jackson3-ptv | mcp-toolsearch-vector | 4/10 +2 invented (budget) | FAIL 0.00 | FAIL 0.11 | 302435 | 5908 | 901 | 20 | 18 | 71.9 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 7/7 | PASS 1.00 | FAIL 0.70 | 50980 | 1611 | 900 | 10 | 8 | 21.7 |
| agentutils-shell-trap | mcp-toolsearch-vector | 5/5 (budget) | PASS 1.00 | PASS 0.85 | 270745 | 4944 | 913 | 25 | 22 | 65.4 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp-toolsearch-vector | 3/4 | 3/4 | 2/4 | 673698 | 45.0 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 3/4 | 4758 | 2482 | 21.8 | 0 |
| jev | 2/4 | 9162 | 175 | 1.4 | 0 |

Agreement: llm vs jev 3/4 | semantic-only llm vs jev 3/4 | llm-semantic vs code checks 4/4 | jev-semantic vs code checks 3/4

## jevjudge-gav / mcp-toolsearch-vector

```
**Maven coordinate (latest released version)**  
```
org.springaicommunity:typesafe-spring-ai:0.3.0
```

**`JevJudge.Builder` – instance methods and their parameters**

| Method | Parameter list (type – name) |
|--------|------------------------------|
| `build()` | *none* |
| `check(String name, java.util.function.Predicate<org.springaicommunity.typesafe.judge.JevJudgeInput> check, String defect)` | `java.lang.String name`, `java.util.function.Predicate<org.springaicommunity.typesafe.judge.JevJudgeInput> check`, `java.lang.String defect` |
| `choice(String name, org.springaicommunity.typesafe.question.Choice choice, String... acceptedOptions)` | `java.lang.String name`, `org.springaicommunity.typesafe.question.Choice choice`, `java.lang.String... acceptedOptions` |
| `criterion(org.springaicommunity.typesafe.judge.JevCriterion criterion)` | `org.springaicommunity.typesafe.judge.JevCriterion criterion` |
| `failFast(boolean failFast)` | `boolean failFast` |
| `failOnError(boolean failOnError)` | `boolean failOnError` |
| `failOnInconclusive(boolean failOnInconclusive)` | `boolean failOnInconclusive` |
| `feedbackRenderer(java.util.function.Function<java.util.List<org.springaicommunity.typesafe.judge.JevFinding>, String> feedbackRenderer)` | `java.util.function.Function<java.util.List<org.springaicommunity.typesafe.judge.JevFinding>, java.lang.String> feedbackRenderer` |
| `minConfidence(double minConfidence)` | `double minConfidence` |
| `noul(String name, org.springaicommunity.typesafe.question.Noul noul, double minimum)` | `java.lang.String name`, `org.springaicommunity.typesafe.question.Noul noul`, `double minimum` |
| `score(String name, org.springaicommunity.typesafe.question.Score score, double minimum)` | `java.lang.String name`, `org.springaicommunity.typesafe.question.Score score`, `double minimum` |

**Default value of `minConfidence`**

`JevJudge.DEFAULT_MIN_CONFIDENCE` = **0.6** (i.e., 60 %).
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant correctly provides the Maven coordinates, enumerates all Builder methods with accurate parameter types, and states the default minConfidence value of 0.6, matching the reference.
- jev: PASS 0.93 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.91, completeness.value=0.96, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jackson3-ptv / mcp-toolsearch-vector

```
**BasicPolymorphicTypeValidator – Builder (Jackson 3.x)**  

The class `com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.Builder` that is returned by the static method  
```java
BasicPolymorphicTypeValidator.builder()
```  
is a **public** class and it exposes the following **public instance methods** (the signatures are taken from the current Jackson 3 release – 3.0.0/3.0.1, which at the time of writing is the latest 3‑series version).

| Method (return type) | Description (brief) |
|----------------------|---------------------|
| `Builder()` | public no‑arg constructor (used by the static `builder()` factory) |
| `Builder allowIfBaseType(Class<?> baseType)` | Accept any sub‑type of the given *base* class. |
| `Builder allowIfBaseType(Class<?> baseType, boolean allowIfSubTypeIsArray)` | Same as above, but the second argument tells the validator whether an **array** of a permitted sub‑type should also be accepted. |
| `Builder allowIfSubType(Class<?> subType)` | Accept a *specific* concrete sub‑type (or any of its sub‑types). |
| `Builder allowIfSubType(Class<?> subType, boolean allowIfSubTypeIsArray)` | Same as above, with explicit control over array handling for that concrete type. |
| `Builder allowIfSubTypeMatches(java.util.function.Predicate<Class<?>> predicate)` | Accept any sub‑type that satisfies the supplied predicate. |
| `Builder allowIfSubTypeIsArray()` | **Special‑purpose** method that tells the validator to treat *array* types themselves as allowed sub‑types, independently of the concrete element type. |
| `PolymorphicTypeValidator build()` | Creates the immutable `PolymorphicTypeValidator` instance that is actually used by `ObjectMapper`. |

---

### Which of these methods does **not** exist in the latest Jackson 2.x release?

All of the methods above exist in the 2‑series *except* **`allowIfSubTypeIsArray()`**.  
That method was introduced together with the “Jackson 3” line of the databind module (see the change‑log entry for version 3.0.0). In the most recent 2‑series release (2.22.3 at the moment of the search) the Builder class does **not** contain `allowIfSubTypeIsArray()`; the only ways to influence array handling in 2.x are the boolean flags that can be passed to the overloads `allowIfBaseType(.., boolean)` and `allowIfSubType(.., boolean)`.

---

### Behaviour change of `allowIfSubTypeIsArray()`

| Jackson version | Default handling of array sub‑types | Effect of calling `allowIfSubTypeIsArray()` |
|-----------------|--------------------------------------|---------------------------------------------|
| **2.x (up to 2.22)** | Arrays of allowed sub‑types are **implicitly allowed**. The method does not exist; array handling is governed only by the `allowIf…(…, boolean)` overloads. | – (method not present) |
| **3.0.0** (first 3‑series) | Arrays are **not allowed** by default. The validator will reject a type identifier that resolves to an array class unless the builder is explicitly instructed otherwise. | Calling `allowIfSubTypeIsArray()` switches the validator on so that *any* array type (e.g. `String[]`, `MyBean[]`) is considered a permissible sub‑type, independent of the element type. This gives the user a clear, opt‑in way to re‑enable the old behaviour. |

**Why the change?**  
Jackson 3 introduced a tighter security model for polymorphic type handling. Implicitly allowing array types was considered a potential attack surface (e.g. deserialising an array of a class that the application never intended to expose). By making array handling opt‑in, developers have to think about it deliberately. The change was introduced together with the new `BasicPolymorphicTypeValidator` implementation in **Jackson 3.0.0** (released 2022‑08‑19). Subsequent patch releases (3.0.1, 3.0.2 …) keep the same semantics.

---

### Summary

* The public Builder API in Jackson 3.x consists of the eight methods listed in the table above.  
* The **only method that is new in the 3‑series and therefore missing from the latest 2‑series (2.22.3)** is `allowIfSubTypeIsArray()`.  
* **Behaviour change:**  
  * **2.x:** array sub‑types are accepted automatically (no explicit method).  
  * **3.0.0+**: array sub‑types are rejected unless the builder is told to allow them via the newly added `allowIfSubTypeIsArray()` method.  

> **What could not be verified** – because the tool budget has been exhausted, the answer relies on the method list that could be obtained from the 2.22.3 Javadoc index and on publicly‑available release notes for Jackson 3.0.0. The exact source code of the Builder class for Jackson 3.x was not inspected directly, so the list of methods is based on the documented public API (and may omit any very recent additions after 3.0.0).
```

- checks: facts 4/10 missing=[Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8] hallucinated=[allowIfSubTypeMatches, allowIf]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Builder() public no‑arg constructor, allowIfBaseType(Class<?>, boolean), allowIfSubType(Class<?>, boolean), allowIfSubTypeMatches(java.util.function.Predicate<Class<?>>), claim that allowIfSubTypeIsArray() does not exist in Jackson 2.x, incorrect method list (missing allowSubTypesWithExplicitDeserializer, denyForExactBaseType, overloads for String/Pattern/TypeMatcher), incorrect description of behaviour change and version numbers], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant's method list and missing‑method claim contradict the reference, and the described behaviour change and versions are wrong, indicating factual errors and major omissions.
- jev: FAIL 0.11 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, grounded.value=0.01, completeness.value=0.22, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "Some of it" (0.65), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [Jackson 3 groupId, Jackson 3 version 3.2.3, allowSubTypesWithExplicitDeserializer, denyForExactBaseType, change version 3.1.4, change version 2.18.8]
- no_invented_names: invented names: [allowIfSubTypeMatches, allowIf]

## spring-ai-hostedtool / mcp-toolsearch-vector

```
**Spring AI OpenAI 2.1.0‑M1 – `HostedTool` implementations**

The sealed interface `org.springframework.ai.openai.responses.HostedTool` permits the following concrete types (all are `record`s that implement the interface):

| Implementation | Brief purpose |
|----------------|----------------|
| `HostedTool.WebSearch` | Web‑search tool (search‑context size, allowed domains). |
| `HostedTool.FileSearch` | Vector‑store file‑search tool (store IDs, max results). |
| `HostedTool.CodeInterpreter` | Run Python code in a sandboxed container. |
| `HostedTool.Mcp` | Call tools on a remote MCP server. |
| `HostedTool.ImageGeneration` | Generate images (model, size). |
| `HostedTool.Raw` | **Raw JSON tool definition** – lets you supply any tool payload as a `Map<String,Object>` that is sent unchanged as the request JSON. |

**Which one sends a raw JSON definition?**  
`HostedTool.Raw` is the “escape hatch” that accepts an arbitrary JSON representation of a tool (e.g., `Map.of("type", "local_shell")`) and forwards it to OpenAI as‑is.
```

- checks: facts 7/7
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The answer correctly lists all six HostedTool records and identifies HostedTool.Raw as the raw JSON option, matching the reference.
- jev: FAIL 0.70 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.60, completeness.value=0.80, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.60, needs at least 0.70)

## agentutils-shell-trap / mcp-toolsearch-vector

```
**`LocalExecBackend` (spring‑ai‑agent‑utils 0.12.0)**  

| Question | Answer (from the Javadoc you’ve already seen) |
|----------|-----------------------------------------------|
| **Default shell on Linux** | The builder’s Javadoc says: “Default: `cmd.exe /c` on Windows, **`/bin/bash -c` elsewhere**.” So on any non‑Windows system – Linux included – a command is run via `/bin/bash -c`. |
| **How to override the shell** | `LocalExecBackend.Builder` has the method `shellCommand(String… shellCommand)`. Supplying your own command line (e.g. `builder.shellCommand("sh", "-c")` or `builder.shellCommand("/usr/bin/zsh", "-c")`) replaces the default `/bin/bash -c`. |
| **Do child processes inherit the JVM’s environment by default?** | The builder also has `cleanEnvironment(boolean cleanEnvironment)`. Its Javadoc explains that when **`cleanEnvironment` is `false` (the default)** the child process **inherits the full JVM environment**, which can include secrets. Setting `cleanEnvironment(true)` starts the child from an empty environment. So, yes – by default child processes inherit the JVM environment. |

---

**`JevJudge.Builder.rubric()` (typesafe‑spring‑ai 0.3.0)**  

The Javadoc for `org.springaicommunity.typesafe.judge.JevJudge.Builder` that we could retrieve lists the following builder methods:

* `check(...)`
* `choice(...)`
* `criterion(...)`
* `failFast(...)`
* `failOnError(...)`
* `failOnInconclusive(...)`
* `feedbackRenderer(...)`
* `minConfidence(...)`
* `noul(...)`
* `score(...)`
* `build()`

No method named **`rubric()`** appears in the public API of the builder class (nor in the enclosing `JevJudge` class). Consequently, based on the documentation we could access, there is no `rubric()` method to call, and we cannot demonstrate its usage.

*What we could not verify:* Whether a `rubric()` method exists in a newer (or older) version of the library, or whether it is a static helper elsewhere (e.g., in a utility class). If such a method exists, it is not part of the `JevJudge.Builder` API shown in version 0.3.0, so the exact call signature and purpose cannot be confirmed from the available Javadoc. If you need a “rubric‑style” construction, you would typically compose the judge by adding criteria with the builder methods shown (`score`, `noul`, `choice`, etc.) and then calling `build()`.
```

- checks: facts 5/5
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=true, semantic_pass=true, code_checks_pass=true} The answer correctly states the default /bin/bash -c shell, the override method, the default inheritance of the JVM environment, and that JevJudge.Builder has no rubric() method, matching the reference.
- jev: PASS 0.85 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED, grounded.value=0.78, completeness.value=0.79, trap_handled.value=0.97, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED]
