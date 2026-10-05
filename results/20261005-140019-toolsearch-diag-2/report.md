# Eval results

Agent: moonshotai.kimi-k2.5 (chat-completions) · LLM judge: deepseek.v3.2 · Jev judge: TypeSafe Jev (client default)

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp | 15/15 | PASS 1.00 | PASS 0.90 | 24973 | 593 | 0 | 6 | 6 | 13.8 |
| jevjudge-gav | mcp-toolsearch | 15/15 | PASS 1.00 | PASS 0.92 | 24865 | 689 | 0 | 7 | 7 | 14.1 |
| jevjudge-gav | mcp-toolsearch-vector | 14/15 | FAIL 0.25 | FAIL 0.35 | 38738 | 962 | 1177 | 11 | 9 | 24.0 |
| jackson3-ptv | mcp | 9/10 | FAIL 1.00 | FAIL 0.76 | 27138 | 1132 | 0 | 5 | 7 | 15.9 |
| jackson3-ptv | mcp-toolsearch | 9/10 | FAIL 1.00 | FAIL 0.83 | 27307 | 1201 | 0 | 7 | 7 | 18.2 |
| jackson3-ptv | mcp-toolsearch-vector | 9/10 | FAIL 1.00 | FAIL 0.93 | 25690 | 896 | 1184 | 6 | 7 | 17.3 |
| spring-ai-hostedtool | mcp | 8/8 | PASS 1.00 | FAIL 0.77 | 209159 | 1417 | 0 | 20 | 19 | 39.1 |
| spring-ai-hostedtool | mcp-toolsearch | 8/8 (budget) | PASS 1.00 | PASS 0.80 | 289167 | 1156 | 0 | 14 | 11 | 33.4 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 2/8 (budget) | FAIL 0.00 | FAIL 0.36 | 301638 | 1558 | 1184 | 26 | 23 | 119.5 |
| agentutils-shell-trap | mcp | 5/5 | PASS 1.00 | PASS 0.83 | 17742 | 717 | 0 | 5 | 8 | 22.2 |
| agentutils-shell-trap | mcp-toolsearch | 5/5 | FAIL 0.38 | FAIL 0.54 | 32113 | 1340 | 0 | 10 | 13 | 25.3 |
| agentutils-shell-trap | mcp-toolsearch-vector | 5/5 | PASS 1.00 | PASS 0.83 | 42992 | 1148 | 1218 | 9 | 13 | 28.8 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| mcp | 3/4 | 3/4 | 2/4 | 282871 | 22.7 |
| mcp-toolsearch | 3/4 | 2/4 | 2/4 | 377838 | 22.7 |
| mcp-toolsearch-vector | 1/4 | 1/4 | 1/4 | 418385 | 47.4 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 6/12 | 12237 | 1206 | 31.5 | 0 |
| jev | 5/12 | 23828 | 525 | 3.2 | 0 |

Agreement: llm vs jev 11/12 | semantic-only llm vs jev 11/12 | llm-semantic vs code checks 8/12 | jev-semantic vs code checks 7/12

## jevjudge-gav / mcp

```
**Maven coordinates:**
```
org.springaicommunity:typesafe-spring-ai:0.4.0
```

**JevJudge.Builder methods with parameter types:**

| Method | Parameters |
|--------|------------|
| `noul(String, Noul, double)` | `String name`, `Noul noul`, `double minimum` |
| `score(String, Score, double)` | `String name`, `Score score`, `double minimum` |
| `choice(String, Choice, String...)` | `String name`, `Choice choice`, `String... acceptedOptions` |
| `check(String, Predicate<JevJudgeInput>, String)` | `String name`, `Predicate<JevJudgeInput> check`, `String defect` |
| `criterion(JevCriterion)` | `JevCriterion criterion` |
| `minConfidence(double)` | `double minConfidence` |
| `failOnInconclusive(boolean)` | `boolean failOnInconclusive` |
| `failOnError(boolean)` | `boolean failOnError` |
| `failFast(boolean)` | `boolean failFast` |
| `feedbackRenderer(Function<List<JevFinding>, String>)` | `Function<List<JevFinding>, String> feedbackRenderer` |
| `build()` | none |

**Default value of `minConfidence`:** `0.6` (60%) — referenced as `JevJudge.DEFAULT_MIN_CONFIDENCE`
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly provides all requested information - Maven coordinates, all Builder methods with parameter types, and the default minConfidence value - exactly matching the reference answer without any contradictions or invented content.
- jev: PASS 0.90 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.87, completeness.value=0.92, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jevjudge-gav / mcp-toolsearch

```
I now have all the information needed.

**Maven coordinates:**
`org.springaicommunity:typesafe-spring-ai:0.4.0`

**Builder methods (parameter types):**

| Method | Parameters |
|--------|------------|
| `noul` | `String name, Noul noul, double minimum` |
| `score` | `String name, Score score, double minimum` |
| `choice` | `String name, Choice choice, String... acceptedOptions` |
| `check` | `String name, Predicate<JevJudgeInput> check, String defect` |
| `criterion` | `JevCriterion criterion` |
| `minConfidence` | `double minConfidence` |
| `failOnInconclusive` | `boolean failOnInconclusive` |
| `failOnError` | `boolean failOnError` |
| `failFast` | `boolean failFast` |
| `feedbackRenderer` | `Function<List<JevFinding>, String> feedbackRenderer` |
| `build` | (none) |

**Default value of `minConfidence`:** `0.6` (60%)
```

- checks: facts 15/15
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant's answer correctly provides all requested information—Maven coordinates, all Builder methods with parameter types, and the default minConfidence value—matching the reference exactly with no errors or omissions.
- jev: PASS 0.92 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.90, completeness.value=0.94, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## jevjudge-gav / mcp-toolsearch-vector

```
I now have all the information I need. Let me compile the final response:

**Maven Coordinates:**
`org.springaicommunity:typesafe-spring-ai:0.2.0`

**JevJudge.Builder Methods with Parameter Types:**

| Method | Parameter Types | Return Type |
|--------|----------------|-------------|
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

**Default value of minConfidence:**
`0.6` (60% - representing a clear majority; the documentation states "a clear majority, 60%, of a score's or choice's probability has to support the verdict before it is acted on")
```

- checks: facts 14/15 missing=[version 0.4.0]
- llm: FAIL 0.25 {factually_consistent=false, completeness=3, hallucinations=[org.springaicommunity:typesafe-spring-ai:0.2.0], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant correctly listed all Builder methods and the default minConfidence value, but provided an incorrect version number (0.2.0 instead of 0.4.0), making it factually inconsistent.
- jev: FAIL 0.35 {grounded=FAILED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.05, completeness.value=0.65, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.05, needs at least 0.60)
- required_facts: missing required facts: [version 0.4.0]

## jackson3-ptv / mcp

```
Now I have all the documentation I need. Let me analyze and compare the public methods:

## Summary

### Public Methods of `BasicPolymorphicTypeValidator.Builder` (Jackson 3.2.3)

| Method | Description |
|--------|-------------|
| `allowIfBaseType(Class<?>)` | Allow when base type is specified class or subclass |
| `allowIfBaseType(Pattern)` | Allow when base type name matches pattern |
| `allowIfBaseType(String)` | Allow when base type name starts with prefix |
| `allowIfBaseType(TypeMatcher)` | Custom base type matcher |
| `allowIfSubType(Class<?>)` | Allow when subtype is specified class or subclass |
| `allowIfSubType(Pattern)` | Allow when subtype name matches pattern |
| `allowIfSubType(String)` | Allow when subtype name starts with prefix |
| `allowIfSubType(TypeMatcher)` | Custom subtype matcher |
| `allowIfSubTypeIsArray()` | Enable validation of Java array sub-types |
| **`allowSubTypesWithExplicitDeserializer()`** | **NEW in Jackson 3.0+** - Allow subtypes with explicit deserializers |
| `denyForExactBaseType(Class<?>)` | Deny exact base type |
| `build()` | Build the validator |

### Method in Jackson 3 that does NOT exist in Jackson 2.22.3

**`allowSubTypesWithExplicitDeserializer()`**

This method was added in Jackson 3.0 and does not exist in Jackson 2.x. The documentation states:
> "Method for appending matcher that will allow all subtypes for which a `ValueDeserializer` is explicitly provided..."

### Behavior Change in `allowIfSubTypeIsArray()`

Both Jackson 3.1.4+ and Jackson 2.18.8+ document the same **security fix**:

> **"NOTE (behavior change in 2.18.8 / 3.1.4 for [databind#5981])"**

**The Change:**
- **Before 2.18.8 (in 2.x) and before 3.1.4 (in 3.x):** The method added a matcher that approved **every array regardless of element type**, allowing an attacker to bypass an explicit sub-class allow-list by wrapping a denied class as an array (e.g., `Evil[]`).

- **Since 2.18.8 / 3.1.4:** The validator now unwraps arrays (recursively for nested arrays) and **validates the innermost element type against configured matchers**. Arrays of primitive, abstract, or interface element types are accepted without explicit allow-list entry, but arrays of concrete classes must have their element types explicitly allowed.

**The fix required calling code to additionally allow-list element types** for arrays they want to accept.

Security-wise, this was a CVE-level fix (databind#5981) - prior versions were vulnerable to gadget chain bypass via array-wrapping.
```

- checks: facts 9/10 missing=[Jackson 3 groupId]
- llm: FAIL 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=false} The assistant's answer correctly lists all public methods, identifies the new method in Jackson 3, and accurately describes the behavior change and versions for allowIfSubTypeIsArray(), matching the reference in all details.
- jev: FAIL 0.76 {grounded=PASSED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.60, completeness.value=0.92, semantic_pass=true} - required_facts: missing required facts: [Jackson 3 groupId]

## jackson3-ptv / mcp-toolsearch

```
Now I have all the information I need. Let me compare the two versions:

**Jackson 3 (3.2.3)** public methods in `BasicPolymorphicTypeValidator.Builder`:
1. `allowIfBaseType(Class<?>)`
2. `allowIfBaseType(Pattern)`
3. `allowIfBaseType(String)`
4. `allowIfBaseType(TypeMatcher)`
5. `denyForExactBaseType(Class<?>)`
6. `allowIfSubType(Class<?>)`
7. `allowIfSubType(Pattern)`
8. `allowIfSubType(String)`
9. `allowIfSubType(TypeMatcher)`
10. `allowIfSubTypeIsArray()`
11. `allowSubTypesWithExplicitDeserializer()` ← **NEW in Jackson 3**
12. `build()`

**Jackson 2.x (2.22.3)** public methods in `BasicPolymorphicTypeValidator.Builder`:
1. `allowIfBaseType(Class<?>)`
2. `allowIfBaseType(Pattern)`
3. `allowIfBaseType(String)`
4. `allowIfBaseType(TypeMatcher)`
5. `denyForExactBaseType(Class<?>)`
6. `allowIfSubType(Class<?>)`
7. `allowIfSubType(Pattern)`
8. `allowIfSubType(String)`
9. `allowIfSubType(TypeMatcher)`
10. `allowIfSubTypeIsArray()`
11. `build()`

The method that exists in Jackson 3 but **NOT in Jackson 2.x** is:
- **`allowSubTypesWithExplicitDeserializer()`**

For the `allowIfSubTypeIsArray()` behavior change:

Both versions document the same behavior change in their Javadocs:
- **Prior behavior**: The method added a matcher that approved **every array regardless of element type**, which allowed attackers to bypass an explicit sub-class allow-list by wrapping a denied class as an array (e.g., `Evil[]`).
- **Changed behavior**: Now the validator unwraps arrays (recursively for nested arrays) and validates the **innermost element type** against the configured sub-class matchers. Arrays of primitive, abstract, or interface element types are accepted without an explicit allow-list entry.

The change was made to address security vulnerability **[databind#5981]**:
- In **Jackson 2.x**: The change was introduced in version **2.18.8**
- In **Jackson 3.x**: The change was introduced in version **3.1.4**

---

**Summary:**

| Question | Answer |
|----------|--------|
| **Method only in Jackson 3** | `allowSubTypesWithExplicitDeserializer()` |
| **Behavior change** | Previously allowed all arrays; now validates array element types against configured matchers |
| **Versions with change** | 2.18.8 (2.x branch) and 3.1.4 (3.x branch) |
| **Issue fixed** | [databind#5981] - Security fix for array-based gadget bypass |
```

- checks: facts 9/10 missing=[Jackson 3 groupId]
- llm: FAIL 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=false} The assistant's answer correctly lists all public methods, identifies the new method in Jackson 3, accurately describes the behavior change of allowIfSubTypeIsArray(), and provides the correct version numbers for the change, matching the reference in all details.
- jev: FAIL 0.83 {grounded=PASSED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.73, completeness.value=0.92, semantic_pass=true} - required_facts: missing required facts: [Jackson 3 groupId]

## jackson3-ptv / mcp-toolsearch-vector

```
Based on my comparison of the Jackson 3.x (3.2.3) and Jackson 2.x (2.22.3) APIs, here's the analysis:

## Public Methods of `BasicPolymorphicTypeValidator.Builder` in Jackson 3

In **Jackson 3.2.3**, the Builder has the following public methods:

1. `allowIfBaseType(Class<?> baseOfBase)`
2. `allowIfBaseType(String prefixForBase)`
3. `allowIfBaseType(Pattern patternForBase)`
4. `allowIfBaseType(BasicPolymorphicTypeValidator.TypeMatcher matcher)`
5. `denyForExactBaseType(Class<?> baseTypeToDeny)`
6. `allowIfSubType(Class<?> subTypeBase)`
7. `allowIfSubType(String prefixForSubType)`
8. `allowIfSubType(Pattern patternForSubType)`
9. `allowIfSubType(BasicPolymorphicTypeValidator.TypeMatcher matcher)`
10. `allowIfSubTypeIsArray()`
11. `allowSubTypesWithExplicitDeserializer()` ← **New in Jackson 3**
12. `build()`

Plus 3 protected helper methods: `_appendBaseMatcher()`, `_appendSubClassMatcher()`, `_appendSubNameMatcher()`

## Method Only in Jackson 3

**`allowSubTypesWithExplicitDeserializer()`** - This method does not exist in the latest Jackson 2.x release (2.22.3). It allows subtypes for which a `ValueDeserializer` is explicitly provided by `jackson-databind` or registered `JacksonModule`s.

## Change to `allowIfSubTypeIsArray()`

The behavior of `allowIfSubTypeIsArray()` changed in **Jackson 2.18.8** (and equivalently in **Jackson 3.1.4**) as documented in [databind#5981]:

**Before 2.18.8 / 3.1.4:** The method added a matcher that approved **every array regardless of element type**, which allowed an attacker to bypass explicit sub-class allow-lists by wrapping a denied class as an array (e.g., `Evil[]`).

**After 2.18.8 / 3.1.4:** The validator now unwraps arrays (recursively for nested arrays) and validates the **innermost element type against the configured sub-class matchers**. Arrays of primitive, abstract, or interface element types are accepted but other element types must be explicitly allow-listed.

Both versions document that callers who relied on "allow every array" must now also allow-list the element types they intend to accept.
```

- checks: facts 9/10 missing=[Jackson 3 groupId]
- llm: FAIL 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=false} The assistant's answer correctly lists all public methods, identifies the new method in Jackson 3, and accurately describes the behavior change and versions for allowIfSubTypeIsArray, matching the reference.
- jev: FAIL 0.93 {grounded=PASSED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.89, completeness.value=0.97, semantic_pass=true} - required_facts: missing required facts: [Jackson 3 groupId]

## spring-ai-hostedtool / mcp

```
Based on my investigation, I need to update the latest release information for you:

## Summary

**Important note:** `HostedTool` does **not exist in the current latest release (2.0.1)** of Spring AI. It was introduced in **version 2.1.0-M1** (a milestone release).

In Spring AI OpenAI module **2.1.0-M1**, the sealed interface `HostedTool` (located in `org.springframework.ai.openai.responses`) permits these types:

| Permitted Type | Description |
|----------------|-------------|
| `HostedTool.WebSearch` | Search the public web |
| `HostedTool.FileSearch` | Search vector stores hosted at OpenAI |
| `HostedTool.CodeInterpreter` | Run Python in a sandboxed container |
| `HostedTool.Mcp` | Call tools on a remote MCP server from inside OpenAI's request |
| `HostedTool.ImageGeneration` | Generate images |
| **`HostedTool.Raw`** | **Escape hatch for sending raw JSON tool definitions** |

### Sending Raw JSON Tool Definitions

**`HostedTool.Raw`** is the type that lets you send a tool definition as raw JSON. It's a record with a single component:

```java
public record Raw(Map<String, Object> tool) implements HostedTool
```

Use this when OpenAI ships new tools that Spring AI hasn't added typed support for yet. For example:
```java
HostedTool rawTool = new HostedTool.Raw(Map.of("type", "some_new_tool", "other_param", value));
```

These are "hosted tools" that OpenAI executes server-side, not `ToolCallbacks` that your application runs. They never appear as tool calls in your application code.
```

- checks: facts 8/8
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant correctly identifies that HostedTool doesn't exist in 2.0.1 but is available in 2.1.0-M1, lists all six permitted types, and correctly identifies Raw as the type for sending raw JSON tool definitions, matching the reference answer completely.
- jev: FAIL 0.77 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.58, completeness.value=0.96, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.58, needs at least 0.60)

## spring-ai-hostedtool / mcp-toolsearch

```
Based on my investigation of **Spring AI OpenAI module version 2.1.0-M1** (the latest pre-release), I can answer your question:

## `HostedTool` Permitted Types

The sealed interface `HostedTool` (in `org.springframework.ai.openai.responses`) permits **6 types**:

1. **`WebSearch`** – Search the public web
2. **`FileSearch`** – Search vector stores hosted at OpenAI
3. **`CodeInterpreter`** – Run Python in a sandboxed container
4. **`Mcp`** – Call tools on a remote MCP server from inside OpenAI's request
5. **`ImageGeneration`** – Generate images
6. **`Raw`** – A tool declared as raw request JSON

## Raw JSON Tool Definition

The **`Raw`** type is the one that lets you send a tool definition as raw JSON:

```java
record Raw(Map<String, Object> tool) implements HostedTool
```

It accepts a `Map<String, Object>` containing the tool definition (e.g., `Map.of("type", "local_shell")`) and converts it to the SDK's `Tool` type using `JsonValue.from(this.tool).convert(Tool.class)`.

---

**I could not verify** whether this exact same API exists in the **2.0.1 release** (the latest stable version), as the `HostedTool` class appears to be a **2.1.0+ addition** based on the `@since 2.1.0` javadoc tag and its location in the `responses` package (likely supporting OpenAI's Responses API).
```

- checks: facts 8/8
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=null, semantic_pass=true, code_checks_pass=true} The assistant correctly identifies the six permitted types of HostedTool, specifies Raw as the one for raw JSON, and properly handles the version ambiguity by explicitly stating 2.1.0-M1 is a pre-release and noting uncertainty about 2.0.1.
- jev: PASS 0.80 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, grounded.value=0.66, completeness.value=0.94, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED]

## spring-ai-hostedtool / mcp-toolsearch-vector

```
I have exceeded the tool budget, so I cannot verify the exact details about `HostedTool`. However, based on my knowledge, I need to be transparent about what I could and could not verify:

**Not Verified:**

I could not verify that a `HostedTool` **sealed interface** exists in Spring AI's OpenAI module (version `org.springframework.ai:spring-ai-openai:2.0.1`). My searches did not locate this type in:
- `spring-ai-openai` 2.0.1
- `spring-ai-core` 1.0.0-M6
- `spring-ai-model` 2.0.1

**What I Found Instead:**

The searches revealed that Spring AI uses different abstractions for tools:
- `ToolCallback` - main interface for tool callbacks
- `FunctionCallback` - for function-based tool calling
- `ToolDefinition` - for tool definitions
- `ToolCallingChatOptions` / `DefaultToolCallingChatOptions` - for tool calling configuration

The search also found `HostedTool` types in the **official OpenAI Java SDK** (`com.openai:openai-java`), but these were related to `HostedToolPermission` (for project-level permissions on hosted tools like Code Interpreter, File Search, etc.), not a sealed interface for sending tool definitions.

**Conclusion:**

Based on my search of the current Spring AI OpenAI module (2.0.1), I **could not verify** that a `HostedTool` sealed interface exists as described. It's possible that:
1. This interface exists in a newer version than what I was able to search
2. It may be in a different artifact (like a model-specific module)
3. The user may be referring to a different library (such as Spring Boot MCP or similar)

I would recommend checking the specific version's Javadoc directly at https://docs.spring.io/spring-ai/reference/ or searching the source code if this is a recently added feature.
```

- checks: facts 2/8 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, ImageGeneration, Raw]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claim that HostedTool does not exist in Spring AI's OpenAI module, Claim that HostedTool types exist only in official OpenAI Java SDK, Listing ToolCallback, FunctionCallback, ToolDefinition, ToolCallingChatOptions as relevant abstractions instead of addressing the sealed interface], trap_handled=null, semantic_pass=false, code_checks_pass=false} The assistant incorrectly denies the existence of HostedTool in Spring AI and provides irrelevant information about different tool abstractions, failing to address the question about the sealed interface's permitted types and Raw type.
- jev: FAIL 0.36 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.44, completeness.value=0.28, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.44, needs at least 0.60)
- completeness: rated "Some of it" (0.83), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, ImageGeneration, Raw]

## agentutils-shell-trap / mcp

```
Based on my examination of the libraries:

## spring-ai-agent-utils 0.13.0 - LocalExecBackend

**Default shell on Linux:** `/bin/bash -c`

**How to override:** Use `Builder.shellCommand(String... shellCommand)` — pass the shell and its flag as varargs, e.g.:
```java
LocalExecBackend backend = LocalExecBackend.builder()
    .shellCommand("/bin/sh", "-c")  // override default bash
    .build();
```

**Environment inheritance:** **Yes**, child processes inherit the JVM's environment **by default** (`cleanEnvironment` defaults to `false`). To start from an empty environment, use:
```java
.builder().cleanEnvironment(true)...
```

---

## typesafe-spring-ai 0.4.0 - JevJudge.Builder

**There is no `rubric()` method** on `JevJudge.Builder`. According to the API, the available methods for adding evaluation criteria are:

| Method | Purpose |
|--------|---------|
| `check(name, Predicate<JevJudgeInput>, defect)` | Code-based checks (not model-judged) |
| `choice(name, Choice, acceptedOptions...)` | Multiple-choice evaluation |
| `noul(name, Noul, minimum)` | Yes/no/uncertain evaluation |
| `score(name, Score, minimum)` | Numeric score threshold |
| `criterion(JevCriterion)` | Add pre-built criterion |

The `rubric()` method you mentioned does not exist in version 0.4.0. You may be thinking of a different class or version, or perhaps the `Score` or `Choice` APIs serve your use case.
```

- checks: facts 5/5
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=true, semantic_pass=true, code_checks_pass=true} The assistant answer correctly states all version numbers, default behaviors, override mechanisms, and explicitly identifies the non-existent rubric() method, matching the reference answer completely without any incorrect claims.
- jev: PASS 0.83 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED, grounded.value=0.79, completeness.value=0.73, trap_handled.value=0.98, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED]

## agentutils-shell-trap / mcp-toolsearch

```
I found the relevant source files. Let me summarize the findings:

## LocalExecBackend (spring-ai-agent-utils 0.13.0)

**Default shell on Linux:** `/bin/bash -c`

**How to override:** Use `LocalExecBackend.Builder.shellCommand(String... shellCommand)`:
```java
LocalExecBackend backend = LocalExecBackend.builder()
    .shellCommand("/bin/sh", "-c")  // Override default shell
    .build();
```

**Environment inheritance:** **No, child processes do NOT inherit the JVM's environment by default.** The `cleanEnvironment` field defaults to `false`, but this is different from full inheritance. When `cleanEnvironment = false` (default), the child still gets a fresh environment. The Javadoc states: "child processes no longer inherit the full JVM environment, including its secrets, unless you opt in".

To explicitly control environment inheritance:
- `.cleanEnvironment(true)` — starts from an empty environment
- `.cleanEnvironment(false)` — default behavior, starts fresh but you can add vars via `.environment(Map)`

---

## JevJudge.Builder.rubric() (typesafe-spring-ai 0.4.0)

**There is NO `rubric()` method on `JevJudge.Builder`.** I verified the entire Builder class in version 0.4.0.

Available methods on `JevJudge.Builder`:
| Method | Purpose |
|--------|---------|
| `.noul(String, Noul, double)` | Adds a boolean/noul criterion |
| `.score(String, Score, double)` | Adds a score-based criterion |
| `.choice(String, Choice, String...)` | Adds a choice-based criterion |
| `.check(String, Predicate<JevJudgeInput>, String)` | Adds a code-based check |
| `.criterion(JevCriterion)` | Adds a pre-built criterion |
| `.minConfidence(double)` | Sets minimum confidence threshold |
| `.failOnInconclusive(boolean)` | Controls inconclusive handling |
| `.failOnError(boolean)` | Controls error handling |
| `.failFast(boolean)` | Skips call after failed check |
| `.feedbackRenderer(Function)` | Custom feedback formatting |

To create a rubric-style evaluation, use the `.score()` method with a `Score` object containing level descriptions:

```java
JevJudge judge = JevJudge.builder(typeSafeClient)
    .score("helpfulness", Score.builder()
        .instructions("How well does the answer address the question?")
        .level("Terrible: irrelevant")
        .level("Mostly unhelpful")
        .level("Mostly helpful")
        .level("Excellent: fully addressed")
        .build(), 2.0)  // minimum passing score
    .build();
```

If you believe `rubric()` should exist, it may be:
1. In a newer version (0.4.0 is the latest as of this lookup)
2. In a different class
3. A method you expected but doesn't exist in this API
```

- checks: facts 5/5
- llm: FAIL 0.38 {factually_consistent=false, completeness=2, hallucinations=["child processes do NOT inherit the JVM's environment by default" contradicts the reference which states they do inherit by default, "When cleanEnvironment = false (default), the child still gets a fresh environment" contradicts the reference which states inheritance occurs], trap_handled=true, semantic_pass=false, code_checks_pass=true} The assistant correctly identifies the default shell and override method, and correctly states JevJudge.Builder has no rubric() method, but makes factually incorrect claims about environment inheritance that contradict the verified reference.
- jev: FAIL 0.54 {grounded=FAILED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED, grounded.value=0.08, completeness.value=0.59, trap_handled.value=0.94, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.08, needs at least 0.60)

## agentutils-shell-trap / mcp-toolsearch-vector

```
Now I have all the information I need. Let me provide the complete answer:

---

## spring-ai-agent-utils (0.13.0) — LocalExecBackend

### Default shell on Linux
**`/bin/bash -c`**

### How to override the shell
Use `LocalExecBackend.Builder.shellCommand(String... shellCommand)`:
```java
LocalExecBackend backend = LocalExecBackend.builder()
    .shellCommand("/bin/sh", "-c")  // or your preferred shell
    .build();
```

### Child process environment inheritance
**By default, child processes DO inherit the JVM's environment** (`cleanEnvironment` defaults to `false`). This preserves historical behavior but is not recommended for model-authored commands since the JVM environment may contain secrets.

To start with a clean environment:
```java
LocalExecBackend backend = LocalExecBackend.builder()
    .cleanEnvironment(true)  // start from empty env
    .build();
```

You can also set specific environment variables via `.environment(Map<String, String>)`.

---

## typesafe-spring-ai (0.4.0) — JevJudge.Builder

**There is no `rubric()` method** on `JevJudge.Builder` in version 0.4.0. The available methods are:

- `noul(String name, Noul noul, double minimum)` — Adds a noul that passes when its truth value reaches the minimum
- `score(String name, Score score, double minimum)` — Adds a score that passes when it reaches the minimum
- `choice(String name, Choice choice, String... acceptedOptions)` — Adds a choice that passes when the selected label is accepted
- `check(String name, Predicate<JevJudgeInput> check, String defect)` — Adds a check answered in code rather than by Jev
- `criterion(JevCriterion criterion)` — Adds a pre-built criterion

If you're looking for a rubric feature, it may not exist in this version or might be under a different API. I could not verify any `rubric()` method in `JevJudge.Builder`.
```

- checks: facts 5/5
- llm: PASS 1.00 {factually_consistent=true, completeness=4, hallucinations=[], trap_handled=true, semantic_pass=true, code_checks_pass=true} The assistant correctly states all facts from the reference answer, explicitly notes the missing rubric() method, and covers all four parts of the question accurately.
- jev: PASS 0.83 {grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED, grounded.value=0.73, completeness.value=0.78, trap_handled.value=0.98, semantic_pass=true} passed=true [grounded=PASSED, completeness=PASSED, required_facts=PASSED, no_invented_names=PASSED, trap_handled=PASSED]
