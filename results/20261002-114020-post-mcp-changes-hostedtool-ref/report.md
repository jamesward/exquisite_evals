# Eval results

| task | arm | checks | llm | jev | in tok | out tok | in-tool tok | model calls | tool calls | time s |
|---|---|---|---|---|---|---|---|---|---|---|
| spring-ai-hostedtool | base | 1/8 | FAIL 0.00 | FAIL 0.17 | 102 | 655 | 0 | 1 | 0 | 11.4 |
| spring-ai-hostedtool | shell | 2/8 (budget) | FAIL 0.08 | FAIL 0.05 | 288063 | 4081 | 0 | 20 | 19 | 57.1 |
| spring-ai-hostedtool | web-brave | 7/8 | FAIL 0.33 | FAIL 0.45 | 101140 | 2014 | 3191 | 13 | 12 | 26.7 |
| spring-ai-hostedtool | mcp | 3/8 | FAIL 0.00 | FAIL 0.36 | 97217 | 4379 | 0 | 28 | 25 | 48.8 |
| spring-ai-hostedtool | mcp-toolsearch | 2/8 (budget) | FAIL 0.00 | FAIL 0.03 | 175076 | 4733 | 0 | 31 | 23 | 49.6 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 2/8 +2 invented | FAIL 0.00 | FAIL 0.11 | 209127 | 5313 | 1191 | 30 | 26 | 70.7 |

## By arm

| arm | checks pass | llm pass | jev pass | total tokens | avg time s |
|---|---|---|---|---|---|
| base | 0/1 | 0/1 | 0/1 | 757 | 11.4 |
| shell | 0/1 | 0/1 | 0/1 | 292144 | 57.1 |
| web-brave | 0/1 | 0/1 | 0/1 | 106345 | 26.7 |
| mcp | 0/1 | 0/1 | 0/1 | 101596 | 48.8 |
| mcp-toolsearch | 0/1 | 0/1 | 0/1 | 179809 | 49.6 |
| mcp-toolsearch-vector | 0/1 | 0/1 | 0/1 | 215631 | 70.7 |

## Judges

| judge | pass rate | in tok | out tok | total time s | errors |
|---|---|---|---|---|---|
| llm | 0/6 | 5404 | 3596 | 34.4 | 0 |
| jev | 0/6 | 13669 | 234 | 2.1 | 0 |

Agreement: llm vs jev 6/6 | semantic-only llm vs jev 6/6 | llm-semantic vs code checks 6/6 | jev-semantic vs code checks 6/6

## spring-ai-hostedtool / base

```
**Answer**

In the current Spring AI OpenAI release the `HostedTool` interface is a *sealed* type that can only be implemented by four concrete classes:

| Permitted type | What it represents |
|----------------|--------------------|
| `FunctionTool` | A classic **function‑calling** tool that is backed by a Spring bean (the bean’s method signature is turned into the OpenAI function definition). |
| `CodeInterpreterTool` | The **code‑interpreter** tool that OpenAI provides out‑of‑the‑box (run Python code, upload files, etc.). |
| `RetrievalTool` | The **retrieval** tool that lets the model query a vector‑store / knowledge base. |
| `RawTool` | A **raw‑JSON** tool definition – you supply the exact JSON that OpenAI expects, without any additional conversion. |

The **`RawTool`** is the one that “lets you send a tool definition as raw JSON”. With it you can hand‑craft the full OpenAI tool schema (name, description, parameters, etc.) and have Spring AI forward that JSON verbatim to the OpenAI API.
```

- checks: facts 1/8 missing=[version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims HostedTool permits FunctionTool, CodeInterpreterTool, RetrievalTool, RawTool, Mentions a RawTool class instead of HostedTool.Raw record, States HostedTool exists in the current release, contradicting that it only exists in the 2.1.0-M1 milestone], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer lists incorrect permitted types and mischaracterizes the release version, failing to match the reference.
- jev: FAIL 0.17 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.01, completeness.value=0.32, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "Some of it" (0.97), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]

## spring-ai-hostedtool / shell

```
**Answer**

In the current (v 2.0.1) release of **Spring AI OpenAI**, the sealed interface  

```java
org.springframework.ai.openai.api.HostedTool
```  

has the following permitted sub‑types:

| Sub‑type (permitted by `HostedTool`) | What it represents |
|--------------------------------------|--------------------|
| **`FunctionTool`** | A tool that is backed by a regular Java `java.util.function.Function` (or a Spring bean annotated with `@Tool`). Spring will translate the method signature into the JSON payload that OpenAI expects. |
| **`RawTool`** (sometimes called **`JsonTool`**) | A tool whose definition is supplied **as raw JSON**. You provide the exact JSON structure (name, description, parameter schema, etc.) that you want OpenAI to receive, and Spring AI sends it unchanged. |

> **Which type lets you send a tool definition as raw JSON?**  
> The **`RawTool`** (aka `JsonTool`) implementation is the one that allows you to pass a pre‑assembled JSON definition directly to the OpenAI API. This is useful when the tool description does not map cleanly onto a Java method or when you want full control over the JSON schema.

---

### What could not be verified directly

- The exact class name of the “raw‑JSON” implementation. The source code for `HostedTool` could not be fetched from the repository in this session, so the answer is based on the public documentation and the naming convention used in the Spring AI release notes. In the source it is called `RawTool`; some older snapshots or the Javadoc may refer to it as `JsonTool`. Both names point to the same concept – a `HostedTool` that carries a raw JSON definition. 

- The full list of permitted sub‑types. The sealed interface currently only has the two implementations above (`FunctionTool` and `RawTool`). If a future upgrade adds additional implementations (e.g., a Kotlin‑specific wrapper), they would also appear in the `permits` clause, but they are not present in the v 2.0.1 release.
```

- checks: facts 2/8 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.08 {factually_consistent=false, completeness=1, hallucinations=[Claims that HostedTool exists in version 2.0.1 (it does not), Places HostedTool in package org.springframework.ai.openai.api instead of org.springframework.ai.openai.responses, States that HostedTool permits only FunctionTool and RawTool, which are not actual implementations, Invents a RawTool (or JsonTool) class for raw JSON instead of the correct HostedTool.Raw record, Misidentifies the set of permitted types (should be WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw)], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer misstates the existence, package, and permitted sub‑types of HostedTool, providing invented class names and an incorrect list, thus it is factually inconsistent and only partially addresses the question.
- jev: FAIL 0.05 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.01, completeness.value=0.09, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "None of it" (0.28), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]

## spring-ai-hostedtool / web-brave

```
**Types permitted by the sealed interface `HostedTool` (Spring AI OpenAI 2.1.0‑M1)**  

- `WebSearch` – web‑search tool  
- `FileSearch` – vector‑store file‑search tool  
- `CodeInterpreter` – sandboxed Python interpreter  
- `Mcp` – remote MCP (Model‑Center‑Protocol) tool  
- `ImageGeneration` – image‑generation tool  
- `Raw` – a tool expressed as raw JSON  

**Tool that lets you send a definition as raw JSON**  
- `HostedTool.Raw` – you supply an arbitrary JSON object (e.g., `Map.of("type","local_shell")`) which is converted directly to the OpenAI `Tool` request.

**Latest release** – Spring AI OpenAI module version **2.1.0‑M1** (published 2026‑09‑24)【repo1.maven.org】.

**Sources**  
- Maven metadata showing latest version 2.1.0‑M1【repo1.maven.org】  
- Source code of `HostedTool` listing the six record types, including `Raw`【raw.githubusercontent.com】.
```

- checks: facts 7/8 missing=[says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it]
- llm: FAIL 0.33 {factually_consistent=false, completeness=4, hallucinations=[Claims that 2.1.0-M1 is the latest release (stable) of the Spring AI OpenAI module, without indicating it is a pre‑release milestone], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer lists the correct six HostedTool record types and identifies Raw for raw JSON, but incorrectly presents 2.1.0-M1 as the latest stable release, contradicting the reference which says it is only a milestone and the latest stable is 2.0.1.
- jev: FAIL 0.45 {grounded=FAILED, completeness=PASSED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.23, completeness.value=0.67, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.23, needs at least 0.70)
- required_facts: missing required facts: [says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it]

## spring-ai-hostedtool / mcp

```
I wasn’t able to locate a `HostedTool` sealed interface in the published sources or Javadoc for the latest Spring AI OpenAI module (the current release on Maven Central is 2.1.0‑M1). Consequently I can’t confirm which concrete types it permits, nor which of those (if any) lets you provide a tool definition as raw JSON. If you have a specific version in mind (for example a non‑milestone release) let me know, and I can look at that exact artifact.
```

- checks: facts 3/8 missing=[WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims that the HostedTool sealed interface does not exist in the 2.1.0-M1 milestone (it does exist)., States that it cannot locate HostedTool or its concrete types in the latest release, contradicting the reference which lists six permitted records.], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer incorrectly says HostedTool is absent in 2.1.0-M1 and provides no information about the six types or the Raw type, directly conflicting with the verified reference.
- jev: FAIL 0.36 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.52, completeness.value=0.21, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.52, needs at least 0.70)
- completeness: rated "Some of it" (0.62), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]

## spring-ai-hostedtool / mcp-toolsearch

```
**Answer**

In Spring AI 2.0.1 (the current release of the **spring‑ai‑openai** module) the `HostedTool` type is a **sealed interface** that can only be implemented by three concrete types:

| Concrete type (permits of `HostedTool`) | What it represents |
|------------------------------------------|---------------------|
| **FunctionTool**                         | A tool that is created from a Java method (or bean) annotated with `@Tool`. Spring AI builds the OpenAI *function* definition from the method signature. |
| **PromptTool**                           | A tool that is backed by a static prompt (a “template” prompt that the model can invoke). |
| **JsonTool** (sometimes called **RawJsonTool**) | A tool whose definition is supplied directly as raw JSON. This lets you hand‑craft the exact OpenAI tool schema without going through the Spring‑AI function‑or‑prompt helpers. |

> **Which type lets you send a tool definition as raw JSON?**  
> The **`JsonTool`** (aka *RawJsonTool*) implementation of `HostedTool` is the one that accepts a raw JSON string containing the tool definition and passes it straight to the OpenAI API.

---

### What could not be verified

* The **exact class names** (`JsonTool` vs. `RawJsonTool`) and the **package** where they live were not retrieved from the source because the tool‑budget has been exhausted. The answer is based on the documented design of the `HostedTool` sealed interface in Spring AI 2.x releases and the typical naming conventions used in the project. If you need the precise fully‑qualified class names, please consult the Javadoc or source for `org.springframework.ai.openai.tool.HostedTool` in the `spring‑ai‑openai` artifact.
```

- checks: facts 2/8 missing=[version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims that HostedTool exists in Spring AI 2.0.1 (it does not exist until 2.1.0-M1), States that HostedTool permits three concrete types (FunctionTool, PromptTool, JsonTool) instead of the six records (WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw), Introduces non‑existent types such as FunctionTool, PromptTool, JsonTool/RawJsonTool, Says JsonTool is the raw‑JSON implementation, whereas the correct raw‑JSON type is HostedTool.Raw], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer contradicts the verified reference on both the existence/version of HostedTool and the permitted types, and invents incorrect class names, making it factually inaccurate and incomplete.
- jev: FAIL 0.03 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=PASSED, grounded.value=0.01, completeness.value=0.04, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "None of it" (0.13), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [version 2.1.0-M1, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]

## spring-ai-hostedtool / mcp-toolsearch-vector

```
**Spring AI OpenAI module – latest release (2.1.0‑M1)**  

The sealed interface **`org.springframework.ai.openai.responses.HostedTool`** has exactly two permitted concrete types:

| Concrete type | What it represents |
|---------------|--------------------|
| **`HostedTool.Function`** (a record) | Wraps a Spring AI **`ToolDefinition`** (name, description, JSON‑schema parameters) and lets the model call a Spring‑managed function. |
| **`HostedTool.RawJson`** (a record) | Holds a **raw JSON string** that is sent verbatim to the OpenAI API as the tool definition. |

> **When you need to send a tool definition as raw JSON, use `HostedTool.RawJson`.** This record bypasses the `ToolDefinition` abstraction and forwards the exact JSON payload you provide to the OpenAI service.
```

- checks: facts 2/8 missing=[says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration] hallucinated=[Function, RawJson]
- llm: FAIL 0.00 {factually_consistent=false, completeness=0, hallucinations=[Claims the sealed interface HostedTool has only two permitted types (Function and RawJson) instead of the six actual records., Invents record names HostedTool.Function and HostedTool.RawJson, which do not exist., States that HostedTool.RawJson holds a raw JSON string, whereas the correct type is HostedTool.Raw with a Map<String,Object>., Treats the 2.1.0-M1 milestone as the latest stable release., Omits the actual six permitted types: WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, and Raw.], trap_handled=null, semantic_pass=false, code_checks_pass=false} The answer provides incorrect type names and counts, misidentifies the raw JSON option, and mischaracterizes the release version, making it both factually inconsistent and incomplete.
- jev: FAIL 0.11 {grounded=FAILED, completeness=FAILED, required_facts=FAILED, no_invented_names=FAILED, grounded.value=0.01, completeness.value=0.21, semantic_pass=false} - grounded: The answer states something expected_output contradicts or does not contain (scored 0.01, needs at least 0.70)
- completeness: rated "Some of it" (0.64), needs to reach 2.00 which is "Most of it"
- required_facts: missing required facts: [says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it, WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration]
- no_invented_names: invented names: [Function, RawJson]
