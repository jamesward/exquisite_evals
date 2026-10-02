# Exquisite Evals

A demo of **AI evals** built with [Spring AI](https://docs.spring.io/spring-ai/reference/2.1/index.html) 2.1.0-M1.
It measures how well an AI agent can work out the API of a Java library it has never seen. Each task needs several
steps in order: find which artifact contains a class, get the Maven coordinates (the GAV), resolve the latest version,
read the class's API, and report exact facts.

The same agent model gets different tool sets (**arms**). Every answer is then graded three ways:

- **Code checks**: code verifies the required facts are present (versions, coordinates, method names, defaults) and flags invented API names.
- **LLM as judge**: a chat model from a different family than the agent compares the answer with a verified reference answer and returns a JSON verdict.
- **Jev as judge**: [`JevJudge`](https://github.com/spring-ai-community/spring-ai-typesafe) asks typed questions (true/false "nouls" and scores) in one call, and adds the code checks as local criteria.

The report compares the arms on accuracy, tokens (in, out, and tokens spent inside tools), model calls, tool calls and
time, and the judges on their verdicts, cost and agreement.

The evals were also used to improve the [javadocs.dev](https://www.javadocs.dev/mcp) MCP server; see
[How evals improved javadocs.dev](#how-evals-improved-javadocsdev).

## Current results

Agent `moonshotai.kimi-k2.5`, LLM judge `deepseek.v3.2`, against the current javadocs.dev, 2026-10-02
(`results/20261002-125459-kimi-k2.5-judge-deepseek-v3.2`). One trial per task × arm, so treat single cells as
indicative only.

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Avg time |
|---|---|---|---|---|---|
| base | 0/4 | 0/4 | 0/4 | 3k | 6 s |
| shell | 0/4 | 0/4 | 0/4 | 798k | 307 s |
| web-brave | 1/4 | 1/4 | 0/4 | 842k | 68 s |
| **mcp** | **3/4** | **2/4** | **2/4** | 484k | 27 s |
| mcp-toolsearch | 2/4 | 2/4 | 2/4 | 184k | 16 s |
| mcp-toolsearch-vector | 2/4 | 2/4 | 1/4 | 440k | 45 s |

- **The javadocs.dev MCP arms are the only ones that pass tasks reliably.** All three solved `jevjudge-gav`, and
  `mcp` passed `jackson3-ptv` (10/10 facts, both judges), the first pass of that task by any arm.
- **The base model makes things up confidently,** and shell and web access cost hundreds of thousands of tokens
  while staying mostly wrong.
- **`spring-ai-hostedtool` is the hardest task.** `HostedTool` exists only in the 2.1.0-M1 milestone, and most
  agents stop at "not in the latest release (2.0.1)" instead of asking for pre-releases.
- **The judges catch what the code checks can't.** On `agentutils-shell-trap/mcp`, Kimi stated every required fact
  but said child processes *don't* inherit the environment (they do). The code checks passed it; both judges failed it.
- **The judges agreed on 22 of 24 answers.** Jev took 6.5 s for all 24, the LLM judge 82 s.

These verdicts used Jev's earlier `grounded` pass bar of 0.7; it is 0.6 from 2026-10-02 (see [Judges](#judges)).
Compared with `gpt-oss-120b` on the same javadocs.dev, Kimi used about 30–50% fewer tokens on the MCP arms in about
half the time ([`results/COMPARISON-models-20261002.md`](results/COMPARISON-models-20261002.md)).

## How evals improved javadocs.dev

The first runs (agent and LLM judge `openai.gpt-oss-120b`) showed the MCP arms doing best, and the transcripts showed
where they still went wrong:

- `search_artifacts("jackson-databind 3.0.0")` returned nothing: a version in the query never matched. One run called
  `search_artifacts` 27 times.
- `symbol_to_artifact("jevjudge")` returned nothing: the symbol index was case-sensitive.
- `list_javadoc_symbols` returned every class of a large library (hundreds for jackson-databind), and the Jackson runs
  used more than 250k input tokens.
- "Latest" was the last entry in `maven-metadata.xml`: publish order, not version order, and pre-releases included
  (Netty resolved to `5.0.0.Alpha2`).

The fixes, deployed 2026-10-02:

- **javadocs.dev MCP:** `search_artifacts` drops version words when the full query matches nothing;
  `symbol_to_artifact` falls back to a case-insensitive match; `list_javadoc_symbols` takes an optional `filter`;
  `get_latest_version` returns the latest *release* by default, with `includePreReleases` to opt in; the tool
  descriptions name the mistakes agents made.
- **zio-mavencentral 0.14.1:** Maven version ordering and pre-release detection, which also fixed the website's
  `/latest` links and badges.

Before → after with `gpt-oss-120b` (full tables in [`results/COMPARISON-20261002.md`](results/COMPARISON-20261002.md)):

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens |
|---|---|---|---|---|
| base | 0/4 → 0/4 | 0/4 → 0/4 | 0/4 → 0/4 | 10k → 10k |
| shell | 0/4 → 0/4 | 0/4 → 0/4 | 0/4 → 0/4 | 643k → 905k |
| web-brave | 1/4 → 0/4 | 1/4 → 0/4 | 0/4 → 0/4 | 903k → 882k |
| mcp | 3/4 → 2/4 | 3/4 → 2/4 | 2/4 → 2/4 | 664k → 688k |
| mcp-toolsearch | 1/4 → **3/4** | 1/4 → 2/4 | 0/4 → 1/4 | 305k → 385k |
| mcp-toolsearch-vector | 3/4 → 0/4 | 3/4 → 0/4 | 2/4 → 0/4 | 674k → 491k |

- **Keyword tool search improved the most.** It now solves `jevjudge-gav` and `agentutils-shell-trap`, and got all
  10 facts on `jackson3-ptv` for the first time. Agents used `filter` in every MCP run.
- **`spring-ai-hostedtool` changed meaning,** so its drop is expected: "latest" is now 2.0.1, which has no
  `HostedTool`, and its reference was rewritten to match. An earlier correct answer ("not in 2.0.1, only in the
  2.1.0-M1 milestone") passes both live judges under the new reference (`SpikeTest` 9).
- **The vector arm's drop is model variance, not search:** on `jevjudge-gav`, `symbol_to_artifact` returned the right
  artifact three times and the model still answered "no artifact contains JevJudge". Several trials per cell are
  needed to separate real changes from noise.
- The "before" side's main run kept only its metrics: its raw results were deleted by a `./gradlew clean`, so its
  `summary.csv` was rebuilt from this README's tables.

## Arms

All arms use the same agent model and the same system prompt, a typical coding-assistant prompt: look things up
instead of relying on memory, resolve the coordinates and latest version first, and say so if something doesn't
exist. Only the tools differ.

| Arm | Tools | Enabled when |
|---|---|---|
| `base` | none: the model's built-in knowledge only | always |
| `shell` | `Bash` + `TodoWrite` from [spring-ai-agent-utils](https://github.com/spring-ai-community/spring-ai-agent-utils), so it can curl, download, unzip and grep jars like a coding assistant. Commands run in a Docker sandbox (see [Running](#running)) | always |
| `web-brave` | Brave `WebSearch` + `WebFetch` (which summarizes each page with the agent model) + shell | `BRAVE_API_KEY` set |
| `web-bedrock` | Bedrock's server-side `web_search` (`external_web_access=false`) + shell | `BEDROCK_WEB_SEARCH_MODEL` set to a GPT‑5.x model your account can use |
| `mcp` | the [javadocs.dev](https://www.javadocs.dev/mcp) MCP tools, all offered up front | always |
| `mcp-toolsearch` | the same MCP tools behind Spring AI's `ToolSearchToolCallingAdvisor`, which shows the model tools only as it searches for them (keyword search: in-process Lucene index) | always |
| `mcp-toolsearch-vector` | the same, with semantic search: `VectorToolIndex` over an in-memory `SimpleVectorStore`, embedding tool descriptions and queries with Cohere Embed v3 (`cohere.embed-english-v3`) on the Bedrock runtime | always |

## Tasks

| Task | What it tests | Reference (short) |
|---|---|---|
| `jevjudge-gav` | Find the artifact from just a class name. Two artifacts contain a `JevJudge`, one of them a Scala library. Then latest version, the Builder API and a constant | `org.springaicommunity:typesafe-spring-ai:0.3.0`, 11 Builder methods, `DEFAULT_MIN_CONFIDENCE = 0.6` |
| `jackson3-ptv` | Stale knowledge: Jackson 3 moved to groupId `tools.jackson.core`. Compare the Builder API between 3.x and 2.x, and a security-related behavior change | 3.2.3 vs 2.22.3; `allowSubTypesWithExplicitDeserializer()` exists only in 3.x; `allowIfSubTypeIsArray()` changed in 3.1.4 / 2.18.8 (databind#5981) |
| `spring-ai-hostedtool` | The class exists only in a milestone: the latest stable release doesn't have it | Not in stable 2.0.1; in the 2.1.0-M1 milestone `HostedTool` permits WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw. Either "not in 2.0.1, see 2.1.0-M1" or "2.1.0-M1 (a milestone)" counts as correct |
| `agentutils-shell-trap` | A default that's easy to miss, plus a method that doesn't exist | 0.12.0; `/bin/bash -c`; `shellCommand(...)`; `cleanEnvironment` defaults to false; there is no `JevJudge.Builder.rubric()` |

The reference answers were checked against the published javadoc and sources jars on 2026-09-30 (`spring-ai-hostedtool`'s
was rewritten on 2026-10-02, after javadocs.dev started resolving "latest" to the latest stable release). Each task
pins the versions its reference depends on: every eval run logs a `STALE REFERENCE` warning when Maven Central has
moved past them, and `./gradlew test -Plive --tests '*ReferenceFreshness*'` fails. Re-verify the reference, then
update the pin in `Tasks.kt`.

## Running

Setup:

1. [Create a Bedrock API key](https://us-east-1.console.aws.amazon.com/bedrock/home?region=us-east-1#/api-keys/long-term/create) and `export AWS_BEARER_TOKEN_BEDROCK=...`
2. `export TYPESAFE_API_KEY=...` for the Jev judge.
3. Optional: `export BRAVE_API_KEY=...` for `web-brave`, and `export BEDROCK_WEB_SEARCH_MODEL=openai.gpt-5.6-terra` for `web-bedrock`.
4. Build the shell sandbox image (needs Docker): `docker build -t exquisite-evals-sandbox:1 sandbox`

The `shell` and web arms run the model's commands in a fresh container per run (`DockerCliExecBackend` from
`spring-ai-agent-utils-docker-cli`), built from `sandbox/Dockerfile`: a JDK plus `curl`, `unzip`, `jq`, `git` and
`python3`, running as a non-root user. Only the run's scratch directory is mounted, at `/workspace`, and none of the
host's environment is passed in, so the commands can't read the API keys. The network is open, since the agent has
to reach Maven Central. `EVALS_SANDBOX=local` runs commands on the host instead (with only `PATH` and `HOME` from its
environment); all runs before 2026-10-02 13:50 ran on the host with its full environment.

Run (needs Java 21+):

```
./gradlew bootRun                                                          # every task x every enabled arm
./gradlew bootRun -PevalTasks=jevjudge-gav,jackson3-ptv -PevalArms=mcp,mcp-toolsearch
./gradlew bootRun -PevalLabel=my-change                                   # name the results directory
./gradlew bootRun --args='--evals.budget.max-tool-calls=15'                # tighter budget
```

Results print to the console and are written to `results/<timestamp>[-<label>]/`: `report.md`, `results.json`
(full answers, every tool call, per-judge criteria), `summary.csv` (one row per task × arm) and `meta.json` (the
models used). `results/` is outside `build/`, so `./gradlew clean` keeps past runs. Compare runs with
`python3 compare.py BEFORE_DIR[,DIR...] AFTER_DIR[,DIR...]`; later directories on a side override earlier ones.

Model settings:

| Env var | Default | What it sets |
|---|---|---|
| `AGENT_MODEL` | `moonshotai.kimi-k2.5` | The agent, for every arm (also WebFetch's page summaries) |
| `AGENT_API` | `chat-completions` | `responses` for gpt-oss; most Bedrock mantle models only support Chat Completions |
| `JUDGE_MODEL` | `deepseek.v3.2` | The LLM judge, always over Chat Completions; keep it a different family from the agent |
| `BEDROCK_MANTLE_BASE_URL` | `https://bedrock-mantle.us-east-1.api.aws/v1` | The GPT‑5.x models use `/openai/v1` |

The earlier runs used `AGENT_API=responses AGENT_MODEL=openai.gpt-oss-120b`, with `openai.gpt-oss-120b` as the LLM
judge too. Embeddings for `mcp-toolsearch-vector` come from the Bedrock runtime (`InvokeModel`) with the same API key,
because mantle has no embedding models (`evals.embedding.model`, `evals.embedding.base-url`); they count as in-tool
tokens.

Run budget (`evals.budget.*`), so an agent stuck in a loop still produces a gradable answer:

| Setting | Default | What happens when it's reached |
|---|---|---|
| `max-tool-calls` | 25 | Further calls to the arm's tools return an error to the model instead of running (`toolSearchTool` isn't counted) |
| `max-model-calls` | 30 | The tools are removed and the model is told to answer with what it has found (marked `(budget)` in the report) |
| `max-input-tokens` | 250,000 | Same as `max-model-calls` (every turn re-sends the whole history) |
| hard stop | max-model-calls + 3 | The run is stopped and recorded as an error |

Garbled tool names (gpt-oss leaks `<|channel|>` markers into them) are resolved to the real tool, unknown tools get an
error the model can recover from, and tool calls with cut-off JSON arguments (Kimi) get an error result and are
repaired in the history, which Bedrock would otherwise reject on the next request.

Tests:

- `./gradlew build` runs offline: no network, no cost.
- `./gradlew test -Plive --tests '*SpikeTest*'` runs live checks of each building block.
- `./gradlew test -Plive --tests '*ReferenceFreshness*'` checks the reference answers are still current.
- `./gradlew test -Plive --tests '*SandboxLiveTest*'` checks the sandbox: tools present, network up, no host keys or files, container removed.

## Judges

| Judge | How it grades | Pass when |
|---|---|---|
| Code checks | Regexes for the required facts; invented names in known families (e.g. any `Jev*` type that doesn't exist); trap phrasing | Every fact present, no invented names |
| LLM judge | One chat call returning `factually_consistent`, `completeness` (0–4), `hallucinations[]`, `trap_handled` | Consistent, completeness ≥ 3, no hallucinations, trap handled, and the code checks pass |
| Jev judge | One `JevJudge` call: `grounded` noul ≥ 0.6, `completeness` score ≥ 2, `trap_handled` noul ≥ 0.7; the code checks as local criteria | Every criterion passes |

Both judges fail an answer that fails the code checks, so their verdicts are comparable.

| Run | LLM judge | Passed (LLM / Jev) | Agreement | Time for all answers (LLM / Jev) | Output tokens (LLM / Jev) |
|---|---|---|---|---|---|
| Baseline, 20 answers | `gpt-oss-120b` | 5 / 2 | 17/20 | 122 s / 5.8 s | 11.4k / 875 |
| Post-change, 24 answers | `gpt-oss-120b` | 4 / 3 | 23/24 | 96 s / 7.2 s | 13.8k / 1.1k |
| Kimi run, 24 answers | `deepseek.v3.2` | 7 / 5 | 22/24 | 82 s / 6.5 s | 3.0k / 1.1k |

- **Both judges catch hallucinations.** Every answer with invented APIs or wrong versions failed both. The LLM judge
  lists the specific wrong claims ("Incorrect Maven coordinates: …typesafe-java-sdk:0.3.0"); Jev's feedback is built
  from the rubric ("grounded: … scored 0.03, needs at least 0.60"), so it is consistent but less specific.
- **Jev's `grounded` bar was lowered from 0.7 to 0.6 on 2026-10-02.** At 0.7 it failed correct answers that added
  detail the reference doesn't mention (scores 0.52–0.68) while the LLM judge passed them; those were most of the
  disagreements. Results before that date were graded at 0.7.
- **The LLM judge doesn't penalize empty answers enough:** it scored an empty answer 0.67. That's why every verdict
  also requires the code checks.
- **Jev was about 5× cheaper and 21× faster** than the `gpt-oss-120b` judge on the baseline run. One Jev call answers
  every criterion together, while the LLM judge writes an explanation for each answer.

### Judge cost (baseline run)

List prices on 2026-09-30, per 1M tokens:

| Model | Input | Output | Source |
|---|---|---|---|
| Jev (TypeSafe) | $0.042 | $0 (not billed; cached input also $0) | Input: [typesafe.ai](https://typesafe.ai/) ("$42 per billion input tokens"). Output and cached input: [Cloudflare's Jev model page](https://developers.cloudflare.com/ai/models/typesafe/jev/) |
| `gpt-oss-120b` on Bedrock (Standard tier) | $0.15 | $0.60 | [Amazon Bedrock pricing](https://aws.amazon.com/bedrock/pricing/); US regions per third-party price trackers (the AWS page lists Sydney at $0.1545 / $0.618) |

| Judge | Input cost | Output cost | Total (20 answers) | Per answer |
|---|---|---|---|---|
| LLM (gpt-oss-120b) | 26,664 tok → $0.0040 | 11,422 tok → $0.0069 | **$0.0108** | $0.00054 |
| Jev | 49,928 tok → $0.0021 | 875 tok → $0 | **$0.0021** | $0.00011 |

- Jev's input is about 3.6× cheaper per token, and its output is free: that's 63% of the LLM judge's bill.
- Jev sends about 1.9× as many input tokens (its state carries the question, answer, reference and tool-call
  trace), which is why the gap is about 5× rather than larger.
- `gpt-oss-120b` is one of the cheapest models on Bedrock; a stronger LLM judge widens the gap, while Jev's cost stays
  the same. These are one run's numbers at list prices; check the providers' pricing pages before relying on them.

## How it works (code map)

| File | What it does |
|---|---|
| `Tasks.kt` | Task definitions: prompt, frozen reference answer, required facts, allowed API names, trap flag, pinned versions |
| `CodeChecks.kt` | Deterministic checks for facts and invented names (normalizes typographic hyphens and spaces) |
| `ReferenceFreshness.kt` | Checks each task's pinned versions against Maven Central (Maven version order) |
| `Arms.kt` | `ArmCatalog`: the tool configuration of each arm; optional arms are enabled from config |
| `WebTools.kt` | Brave search + fetch; Bedrock hosted `web_search` |
| `Embeddings.kt` | Cohere Embed v3 on the Bedrock runtime (document vs query input types, token accounting) |
| `Judges.kt` | `LlmJudge` and `JevAsJudge` (both include the code checks) |
| `RunBudget.kt` | Tool-call cap, recovery from garbled tool names, handling of unknown tools |
| `Sandboxes.kt` | The per-run Docker container the shell arms' commands run in (`sandbox/Dockerfile`) |
| `ToolCallArguments.kt` | Error results for, and history repair of, tool calls with unparseable JSON arguments |
| `TokenTracking.kt` | Per-turn token and tool accounting; forces the wrap-up answer when the budget is used up |
| `EvalRunner.kt` | Runs the task × arm matrix, calls both judges, writes the report |
| `compare.py` | Before/after tables from two sets of result directories |

## Caveats

- Every result here is from one trial per task × arm; model output varies between runs, so treat them as indicative.
- The reference answers were checked through javadocs.dev and the sources jars, and javadocs.dev is also what the MCP
  arms use. The facts came from the published artifacts, not from how javadocs.dev renders them.
- `web-bedrock` hasn't been measured: this account can't use the GPT‑5.x models it needs. The arm is ready and turns
  on when `BEDROCK_WEB_SEARCH_MODEL` is set.
- **Shell-arm results before 2026-10-02 13:50 ran on the host,** with its environment and tools; later runs use the
  Docker sandbox, so shell-arm numbers across that change aren't strictly comparable. The sandbox isolates files
  and secrets, not the network.

## Appendix: gpt-oss-120b runs

### Baseline, every run (2026-09-30)

`results/20260930-133320-pre-mcp-changes`. Columns:

- **Checks:** required facts found / total. `+N invented` means N API names that don't exist; `(budget)` means the final answer was forced.
- **LLM / Jev:** pass or fail, with the judge's 0–1 score.
- **In-tool tok:** tokens spent by model calls inside tools (WebFetch page summaries).

| Task | Arm | Checks | LLM | Jev | In tok | Out tok | In-tool tok | Model calls | Tool calls | Time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | base | 2/15 | FAIL 0.00 | FAIL 0.04 | 119 | 1,612 | 0 | 1 | 0 | 27.1 |
| jevjudge-gav | shell | 1/15 | FAIL 0.00 | FAIL 0.05 | 25,857 | 951 | 0 | 6 | 5 | 12.9 |
| jevjudge-gav | web-brave | 14/15 | FAIL 0.25 | FAIL 0.32 | 235,113 | 4,375 | 15,224 | 27 | 25 | 75.2 |
| jevjudge-gav | mcp | 15/15 | PASS 1.00 | PASS 0.92 | 38,643 | 1,704 | 0 | 11 | 10 | 16.8 |
| jevjudge-gav | mcp-toolsearch | 1/15 | FAIL 0.00 | FAIL 0.04 | 30,057 | 2,782 | 0 | 18 | 10 | 48.0 |
| jackson3-ptv | base | 3/10 +18 invented | FAIL 0.08 | FAIL 0.13 | 126 | 4,167 | 0 | 1 | 0 | 29.8 |
| jackson3-ptv | shell | 6/10 (budget) | FAIL 0.25 | FAIL 0.26 | 292,599 | 5,999 | 0 | 20 | 19 | 63.8 |
| jackson3-ptv | web-brave | 3/10 +2 invented | FAIL 0.08 | FAIL 0.18 | 227,320 | 6,454 | 46,975 | 27 | 25 | 97.0 |
| jackson3-ptv | mcp | 6/10 (budget) | FAIL 0.17 | FAIL 0.22 | 312,897 | 6,866 | 0 | 24 | 23 | 79.1 |
| jackson3-ptv | mcp-toolsearch | 0/10 | FAIL 0.67 | FAIL 0.26 | 8,451 | 1,001 | 0 | 6 | 4 | 8.5 |
| spring-ai-hostedtool | base | 1/7 | FAIL 0.00 | FAIL 0.13 | 102 | 1,239 | 0 | 1 | 0 | 18.3 |
| spring-ai-hostedtool | shell | 6/7 | FAIL 0.33 | FAIL 0.40 | 159,497 | 3,458 | 0 | 25 | 24 | 78.5 |
| spring-ai-hostedtool | web-brave | 3/7 | FAIL 0.08 | FAIL 0.17 | 72,938 | 2,111 | 22,764 | 12 | 11 | 35.4 |
| spring-ai-hostedtool | mcp | 7/7 | PASS 1.00 | FAIL 0.74 | 16,019 | 842 | 0 | 5 | 4 | 11.2 |
| spring-ai-hostedtool | mcp-toolsearch | 7/7 | PASS 1.00 | FAIL 0.72 | 17,453 | 1,212 | 0 | 7 | 4 | 14.1 |
| agentutils-shell-trap | base | 2/5 +1 invented | FAIL 0.13 | FAIL 0.11 | 128 | 2,355 | 0 | 1 | 0 | 35.8 |
| agentutils-shell-trap | shell | 1/5 | FAIL 0.06 | FAIL 0.07 | 150,147 | 4,158 | 0 | 27 | 25 | 54.6 |
| agentutils-shell-trap | web-brave | 5/5 | PASS 1.00 | FAIL 0.77 | 251,103 | 5,044 | 13,642 | 28 | 25 | 91.8 |
| agentutils-shell-trap | mcp | 5/5 (budget) | PASS 1.00 | PASS 0.83 | 282,131 | 5,078 | 0 | 20 | 19 | 64.5 |
| agentutils-shell-trap | mcp-toolsearch | 3/5 | FAIL 1.00 | FAIL 0.74 | 238,742 | 4,914 | 0 | 27 | 18 | 56.7 |

At that time, `spring-ai-hostedtool`'s reference called 2.1.0-M1 "latest" (javadocs.dev's behavior then), and the
trap check missed the phrasing "does not contain a `rubric()` method" (since fixed; the table keeps the original
verdict).

### Baseline by task

- **`jevjudge-gav`:** `mcp` solved it in 11 model calls (`symbol_to_artifact` → `get_latest_version` →
  `list_javadoc_symbols` → `get_javadoc_symbol`, 15/15 facts). `web-brave` got the complete Builder API but the SDK
  artifact (`typesafe-java-sdk`). `shell` and `mcp-toolsearch` concluded the class doesn't exist. `base` invented
  `com.github.patrickfav:jev-judge:1.4.0`.
- **`jackson3-ptv`:** no arm passed. The tool-using arms kept searching `com.fasterxml.jackson.core` for a 3.x release,
  the stale knowledge this task exposes; `mcp` and `shell` used more than 250k input tokens before being made to
  answer. `web-brave` invented `allowIfSubTypeIsCollection`; `base` invented 18 methods.
- **`spring-ai-hostedtool`:** both MCP arms listed all six records and `Raw`. `shell` attributed them to the wrong
  version; `web-brave` gave wrong type names from a blog-style source; `base` invented `RestTool` / `RawTool`.
- **`agentutils-shell-trap`:** `mcp` and `web-brave` got everything right, including that `rubric()` doesn't exist.
  `shell` and `base` fell for the trap and gave the default shell as `/bin/sh`.

### Tool search: keyword vs embeddings (baseline)

`mcp-toolsearch-vector` was measured in a separate run on 2026-09-30 (`results/20260930-142417-pre-mcp-changes-vector`):

| Task | Arm | Checks | LLM | Jev | In tok | Out tok | In-tool tok | Model calls | Tool calls | Time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp-toolsearch-vector | 15/15 | PASS 1.00 | PASS 0.93 | 31,703 | 1,751 | 907 | 8 | 5 | 20.9 |
| jackson3-ptv | mcp-toolsearch-vector | 4/10 +2 invented (budget) | FAIL 0.00 | FAIL 0.11 | 302,435 | 5,908 | 901 | 20 | 18 | 71.9 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 7/7 | PASS 1.00 | FAIL 0.70 | 50,980 | 1,611 | 900 | 10 | 8 | 21.7 |
| agentutils-shell-trap | mcp-toolsearch-vector | 5/5 (budget) | PASS 1.00 | PASS 0.85 | 270,745 | 4,944 | 913 | 25 | 22 | 65.4 |

- **Same accuracy as offering every tool, at about the same cost.** The embeddings themselves cost about 900 tokens
  per task, for indexing 8 tool descriptions and embedding each search query.
- **The embeddings separate these tools only weakly.** In a live check (`SpikeTest` 8), "which Maven artifact
  contains this Java class?" ranked `symbol_to_artifact` 4th, with all 8 tools scoring 0.49–0.59, because every
  description talks about Maven artifacts and javadoc. With `maxResults(5)`, it still got through.
- **Tool search is below its sweet spot here.** The MCP server has 8 tools, under the roughly 10 where Spring AI
  recommends tool search, so the extra search step adds turns and room for mistakes.
