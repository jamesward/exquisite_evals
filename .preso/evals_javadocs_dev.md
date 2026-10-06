---
marp: true
theme: default
paginate: true
style: |
  section { font-size: 27px; }
  section.lead h1 { font-size: 58px; }
  table { font-size: 21px; }
  pre { font-size: 17px; line-height: 1.25; }
  .small { font-size: 20px; }
---

<!-- _class: lead -->
<!-- _paginate: false -->

# Exquisite Evals for Skills & MCPs

### How we used Spring AI, Jev, & Evals to make javadocs.dev better for agents

James Ward - jamesward.com - AWS & AAIF TC
Christian Tzolov - Spring AI Lead @ Broadcom & AAIF Ambassador

---

## 3D Evals: Task, Arm, Judge

for t in task:
  for a in arm:
    metrics


    for j in judge:
      metrics

???

---

## What is an eval?

A **test for a system whose output isn't deterministic**, like an AI agent.

- **System under test**: the model + prompt + tools you want to measure
- **Task**: an input, plus what a good answer must contain (a reference answer)
- **Grader**: code, another model, or a person decides pass or fail
- **Meta Metrics**: accuracy, hallucinations, tokens (cost), time, tool calls

Run the same tasks on each variant/arm, compare the numbers, change one thing, run again.

<!--
Unit tests assert an exact output. Evals assert properties of outputs that vary from run to run,
and they compare variants rather than just passing or failing one build.
-->

---

## Arm

???

---

## Judge

| Grader | Good at | Watch out for |
|---|---|---|
| **Code checks** | Exact facts: versions, coordinates, method names, "says X doesn't exist" | Brittle wording and typography |
| **LLM as judge** | Meaning, completeness, explaining what's wrong | Cost, leniency, varies run to run |
| **Typed judge model** ([Jev](https://typesafe.ai/)) | Many yes/no and score questions in one cheap, fast call, Parallel | Thresholds are a design decision |

Combine them: code for what code can settle, a judge for the rest.

---

## What is Jev / System One / Decision Models?



---

## The question

**Does the [javadocs.dev](https://www.javadocs.dev/mcp) MCP server help an agent understand a Java library it has never seen?**

Compared with: no tools · a shell (curl, unzip, grep jars) · web search + fetch

Every task needs a chain of steps:

class name → artifact (GAV) → latest version → read the API → report exact facts

---

## Four tasks

| Task | What it tests |
|---|---|
| `jevjudge-gav` | Find the artifact from just a class name. A Java and a Scala library both have a `JevJudge` |
| `jackson3-ptv` | Stale knowledge: Jackson 3 moved to groupId `tools.jackson.core`. Diff the 3.x and 2.x API |
| `spring-ai-hostedtool` | The class only exists in a milestone release (2.1.0-M1) |
| `agentutils-shell-trap` | A default that's easy to miss, plus a method that doesn't exist |

Reference answers were checked against the published javadoc and sources jars.

---

## Six arms, one model

Same model (`gpt-oss-120b`, later `kimi-k2.5`, on Amazon Bedrock), same coding-assistant system prompt. Only the tools differ.

| Arm | Tools |
|---|---|
| `base` | None |
| `shell` | `Bash` + `TodoWrite` (spring-ai-agent-utils) |
| `web-brave` | Brave search + WebFetch + shell |
| `mcp` | The 8 javadocs.dev MCP tools, all offered up front |

<span class="small">Also ready: `web-bedrock`, Bedrock's hosted web search, once the account has GPT‑5.x access.</span>

---

## Baseline results

| Arm | Code checks | LLM judge | Jev | Total tokens | Avg time |
|---|---|---|---|---|---|
| base | 0/4 | 0/4 | 0/4 | 10k | 28 s |
| shell | 0/4 | 0/4 | 0/4 | 643k | 52 s |
| web-brave | 1/4 | 1/4 | 0/4 | 903k | 75 s |
| **mcp** | **3/4** | **3/4** | **2/4** | 664k | 43 s |

- The base model is fluent and wrong: invented coordinates, 18 Jackson methods, a `rubric()` API
- Shell and web burned hundreds of thousands of tokens and were still mostly wrong
- No arm solved Jackson 3

<span class="small">One trial per task × arm, so indicative only. *Measured in a separate run.</span>

---

## Reading the transcripts: where agents went wrong

The scores say *that* the javadocs.dev arms failed; the tool calls say *why*.

- `search_artifacts("jackson-databind 3.0.0")` → `[]`: a version in the query never matched. One run called `search_artifacts` **27 times**.
- `symbol_to_artifact("jevjudge")` → `[]`: the symbol index was case-sensitive.
- `list_javadoc_symbols` lists every class (hundreds for jackson-databind), and the Jackson runs used **250k+ input tokens**.
- Embedding search ranked `symbol_to_artifact` only **4th** for "which Maven artifact contains this Java class?"

---

## A bug hiding under "latest"

"Latest" was the **last entry in `maven-metadata.xml`**.

- That's **publish order**, not version order: jackson-databind lists `2.4.1.3` *after* `2.4.2` (14 such pairs)
- It **included pre-releases**: Netty → `5.0.0.Alpha2`, Hibernate → `8.0.0.Beta3`

Agents trust "latest", so a wrong latest sends them to the wrong API. It also affected the website's `/latest` links and badges.

---

## What we changed

**javadocs.dev MCP server**
- `search_artifacts`: ignore version words when the full query matches nothing
- `symbol_to_artifact`: case-insensitive fallback before the slow AI search
- `list_javadoc_symbols`: new optional `filter`, e.g. `"PolymorphicTypeValidator"`
- `get_latest_version`: latest *release* by default; `includePreReleases` to opt in
- Descriptions name what agents got wrong: groupId moves, several artifacts per class, milestones

**zio-mavencentral 0.14.1**
- Maven version ordering (coursier `versions`), `isPreRelease`, `latest(includePreReleases)`
- The website's `/latest` and badges: releases only

---

## Did it help?

???

---

## Then: swap the model

Same tasks, same javadocs.dev. Agent `gpt-oss-120b` → `kimi-k2.5`; LLM judge → `deepseek-v3.2`, a different family from the agent.

| Arm | Code checks | Jev | Total tokens | Avg time |
|---|---|---|---|---|
| mcp | 2/4 → 3/4 | 2/4 → 2/4 | 688k → 484k | 62 → 27 s |
| shell | 0/4 → 0/4 | 0/4 → 0/4 | 905k → 798k | 126 → 307 s |

- First pass on `jackson3-ptv` (Kimi + `mcp`). The MCP arms used fewer tokens and took about half the time
- The judges caught a wrong claim ("child processes don't inherit the environment") that passed the code checks

---

## Evals evaluate your evaluator, too

Bugs we found in the eval harness itself:

- Token tracking silently skipped tool-free calls (an advisor-order tie)
- The LLM judge gave an **empty answer** a score of 0.67 ("nothing wrong was said")
- `2.1.0‑M1` written with a non-breaking hyphen failed a code check
- A trap check missed "does not contain a `rubric()` method"
- gpt-oss leaked `<|channel|>` markers into tool names, which crashed runs
- Agents looped until stopped, so we added a per-run budget
- `./gradlew clean` deleted the baseline results, so results now live in `results/`

Read the transcripts, and check the checks.

---

## Architecture

```text
 Tasks.kt (prompt, reference, facts)      Arms.kt (ArmCatalog: tools + tool-calling advisor)
                     \                      /
                      v                    v
                    EvalRunner: every task x every enabled arm
                              |
                              v
   ChatClient  (Spring AI 2.1.0-M1, OpenAiResponsesChatModel, gpt-oss-120b on Bedrock mantle)
     |- tools for the arm: none | Bash | Brave + WebFetch | javadocs.dev MCP client
     |- ToolCallingAdvisor | ToolSearchToolCallingAdvisor (Lucene | Cohere vectors)
     '- TokenTrackingAdvisor: tokens + tools per turn, run budget
                              |
                              v
        RunRecord: answer, every tool call, tokens (incl. in-tool), model calls, time
                              |
              +---------------+----------------+
              v               v                v
          CodeChecks       LlmJudge        JevAsJudge
              +---------------+----------------+
                              v
      results/<timestamp>-<label>/  report.md · results.json · summary.csv  -->  compare.py
```

---

## One run, with a budget

Agents can loop forever, so each run gets a budget and still ends with a gradable answer:

| Limit | Default | When reached |
|---|---|---|
| Tool calls | 25 | Further calls return an error to the model |
| Model calls | 30 | Tools removed; the model is told to answer with what it found |
| Input tokens | 250,000 | Same (every turn re-sends the whole history) |
| Hard stop | model calls + 3 | The run is recorded as an error |

Garbled or unknown tool names, and cut-off JSON arguments, are answered with an error instead of crashing the run.

Shell commands run in a **Docker sandbox** (one container per run, only `/workspace` mounted, no host env, so no API keys).

---

## Three graders, one verdict

**Code checks**: required facts (regex), invented names (e.g. any `Jev*` type that doesn't exist), trap phrasing

**LLM judge** (`gpt-oss-120b`, later `deepseek-v3.2`): a JSON verdict with `factually_consistent`, `completeness` 0–4, `hallucinations[]`, `trap_handled`

**Jev judge** (`JevJudge`, one call):
- `grounded` noul ≥ 0.7 · `completeness` score ≥ 2 · `trap_handled` noul ≥ 0.7
- the code checks added as local criteria, never sent to Jev

Both judges fail an answer that fails the code checks, so their verdicts are comparable.

---

## Judges compared (baseline, 20 answers)

| | LLM judge (gpt-oss-120b) | Jev |
|---|---|---|
| Passed | 5/20 | 2/20 |
| Same verdict | 17/20 | 17/20 |
| Total time | 122 s | 5.8 s |
| Cost | $0.0108 | $0.0021 |

- Jev is stricter on `grounded`: it failed three correct answers that added extra detail (0.52–0.65, bar 0.7)
- The LLM judge is lenient on empty answers
- Jev doesn't charge for output tokens, and input costs $0.042 vs $0.15 per 1M tokens

---

## Takeaways

- **Measure, don't trust:** the base model is confident and wrong
- **Tools beat prompts here:** `symbol_to_artifact` answered "which artifact has this class?" in one call
- **Transcripts are the real output of an eval:** they told us what to fix in the server
- **Combine graders, then test the graders**
- **Keep every run:** before and after is the whole point

---

## Links

- javadocs.dev: https://www.javadocs.dev/
- Spring AI 2.1 reference: https://docs.spring.io/spring-ai/reference/2.1/
- spring-ai-agent-utils: https://github.com/spring-ai-community/spring-ai-agent-utils
- spring-ai-typesafe (JevJudge): https://github.com/spring-ai-community/spring-ai-typesafe
- 
