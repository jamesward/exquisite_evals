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

## Results

Agent `moonshotai.kimi-k2.5` on Amazon Bedrock, LLM judge `deepseek.v3.2`, Jev, shell arms in the Docker sandbox.
One trial per task × arm, so treat single cells as indicative only.

### Baseline: javadocs.dev before the changes (2026-10-06)

`results/20261006-075758-pre-mcp-changes-clean`, against the javadocs.dev MCP server as it was before the changes
described [below](#how-evals-improved-javadocsdev).

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Model calls | Tool calls | Avg time |
|---|---|---|---|---|---|---|---|
| base | 0/4 | 0/4 | 0/4 | 4k | 4 | 0 | 13 s |
| shell | 1/4 | 1/4 | 1/4 | 812k | 87 | 100 | 318 s |
| web-brave | 0/4 | 0/4 | 0/4 | 735k | 49 | 73 | 71 s |
| **mcp** | **3/4** | **3/4** | **3/4** | 254k | 27 | 31 | 19 s |

Per task: facts found, then pass (P) or fail (.) for code checks / LLM judge / Jev, then total tokens.

| Task | base | shell | web-brave | mcp |
|---|---|---|---|---|
| jevjudge-gav | 1/15 ... 0k | 0/15 ... 243k | 14/15 ... 141k | **15/15 PPP 39k** |
| jackson3-ptv | 3/10 ... 1k | 6/10 ... 232k | 9/10 ... 264k | **10/10 PPP 144k** |
| spring-ai-hostedtool | 2/8 ... 1k | **8/8 PPP 200k** | 6/8 ... 113k | 7/8 ... 23k |
| agentutils-shell-trap | 0/5 ... 1k | 1/5 ... 135k | 1/5 ... 215k | **5/5 PPP 46k** |

- **`mcp` solved 3 of 4 tasks with a third of the tokens** of the shell and web arms, in 19 s per task on average.
- **The base model pretends to use tools.** It writes `<tool>web_search</tool>` into its answer, then invents the
  "results": `com.github.h-thurow:jev:0.3.0`, `withMinConfidence(double)`, and a `HostedTool` that doesn't exist.
- **Shell and web spent 735k–812k tokens and stayed mostly wrong.** Shell concluded that `JevJudge` and
  `spring-ai-agent-utils` don't exist; web invented an `AGENT_SHELL_COMMAND` variable for overriding the shell. Shell
  solved `spring-ai-hostedtool` by downloading the 2.1.0-M1 sources and calling it a milestone.
- **`mcp` failed `spring-ai-hostedtool` on "latest".** The server's `get_latest_version` returned the milestone
  `2.1.0-M1`, and the answer gave it as the version without saying it isn't a release.
- **The judges agreed on all 16 answers,** and no run errored.

### After: javadocs.dev with the changes (2026-10-06)

`results/20261006-085428-post-mcp-changes-clean`, the same tasks, arms, models, sandbox and budgets. Both runs used
`-Pinspector`. A direct preflight verified all four server changes; the run transcript shows filtered symbol lookups,
release-only latest and calls with the new `includePreReleases` parameter.

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Model calls | Tool calls | Avg time |
|---|---|---|---|---|---|---|---|
| base | 0/4 | 0/4 | 0/4 | 3k | 4 | 0 | 7 s |
| shell | 0/4 | 0/4 | 0/4 | 744k | 81 | 93 | 261 s |
| web-brave | 0/4 | 0/4 | 0/4 | 753k | 55 | 69 | 58 s |
| **mcp** | **2/4** | **2/4** | **1/4** | 564k | 44 | 50 | 44 s |

Per task for `mcp`:

| Task | Before | After | What changed |
|---|---|---|---|
| jevjudge-gav | 15/15 PPP, 39k | 15/15 PPP, 26k | `filter="JevJudge"`; 9 → 6 tool calls |
| jackson3-ptv | 10/10 PPP, 144k | 8/10 ..., 109k | filtered symbol lists cut the largest turn from 76k to 26k tokens; the answer omitted two facts |
| spring-ai-hostedtool | 7/8 ..., 23k | 2/8 ..., 407k (budget) | stable `2.0.1` was correct, but the agent never opted into pre-releases and wandered for 21 tool calls |
| agentutils-shell-trap | 5/5 PPP, 46k | 5/5 PP., 21k | filtered lookups halved tokens; Jev alone rejected the correct answer (`grounded=0.48`) |

- **The server changes worked mechanically:** a preflight verified release-only latest, case-insensitive symbol lookup,
  version-word fallback and filtered class lists; the new run used the smaller filtered results. On the three comparable MCP tasks, input fell from 228k to
  155k (32%), and their largest turns were much smaller.
- **The aggregate score did not improve in this one trial.** `mcp` went from 3/4 to 2/4 by code checks and the LLM
  judge, and total tokens rose because `spring-ai-hostedtool` alone used 407k. After learning that stable 2.0.1 has
  no `HostedTool`, the agent searched unrelated artifacts instead of calling `get_latest_version` with
  `includePreReleases=true` to inspect 2.1.0-M1.
- **The Jackson failure is answer variance, not missing evidence.** The tools returned both coordinates, versions and
  the filtered API pages, but the final answer omitted `tools.jackson.core` and gave the wrong change version.
- **The non-MCP arms are controls, not effects of the server change.** Their movement between runs demonstrates why
  one trial per cell cannot establish a performance change.

Full diff: [`results/COMPARISON-clean-20261006.md`](results/COMPARISON-clean-20261006.md).

A five-trial follow-up on `spring-ai-hostedtool` / `mcp` confirms the variance: only 1/5 trials found and inspected
2.1.0-M1. The best trial got 8/8 facts and passed the code and LLM judges using 217k tokens, but Jev scored
`grounded=0.59`, just below its 0.60 bar. The other four trials missed required facts; median usage was 312k tokens.
The aggregate table above remains the single matched before/after run—using only the best follow-up would be
optimistic selection. An extra trial after rolling back to the old server reproduced its original result almost
exactly: 7/8 facts, 24k tokens and 5 tool calls, again failing only because it called 2.1.0-M1 a release rather than
a milestone. Details: [`results/COMPARISON-post-mcp-hostedtool-5trials-20261006.md`](results/COMPARISON-post-mcp-hostedtool-5trials-20261006.md).

## How evals improved javadocs.dev

The baseline transcripts, and calls to the same server directly, show where the `mcp` arm went wrong or wasted tokens:

- **"Latest" included pre-releases, in publish order.** It was the last entry in `maven-metadata.xml`:
  `spring-ai-openai` resolved to the milestone `2.1.0-M1`, which cost `mcp` the `spring-ai-hostedtool` task, and Netty
  to `5.0.0.Alpha2`.
- **`list_javadoc_symbols` returns every class.** For jackson-databind that's 155k characters for 3.2.3 and 134k for
  2.22.3; one turn sent 76k input tokens, and the Jackson task alone used 143k of `mcp`'s 254k tokens.
- **`search_artifacts` misses class names and versions.** `"JevJudge"`, `"judge"` and `"jev"` all returned nothing
  after `symbol_to_artifact` had already found the artifact, and `"jackson-databind 3.0.0"` returns nothing because
  the version never matches.
- **`symbol_to_artifact` is case-sensitive:** `"jevjudge"` returns nothing.

The changes:

- **javadocs.dev MCP:** `search_artifacts` drops version words when the full query matches nothing;
  `symbol_to_artifact` falls back to a case-insensitive match; `list_javadoc_symbols` takes an optional `filter`;
  `get_latest_version` returns the latest *release* by default, with `includePreReleases` to opt in; the tool
  descriptions name the mistakes agents made.
- **zio-mavencentral 0.14.1:** Maven version ordering and pre-release detection, which also fixed the website's
  `/latest` links and badges.

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
| `mcp-toolsearch-jev` | the same, with TypeSafe's `JevToolIndex` from typesafe-spring-ai: one Jev call per search judges which tools perform the requested task, and can return none (applicability threshold 0.5, minimum relevance 0.05). Its calls aren't counted as in-tool tokens, since the index doesn't expose their usage | `TYPESAFE_API_KEY` set |

## Tasks

| Task | What it tests | Reference (short) |
|---|---|---|
| `jevjudge-gav` | Find the artifact from just a class name. Two artifacts contain a `JevJudge`, one of them a Scala library. Then latest version, the Builder API and a constant | `org.springaicommunity:typesafe-spring-ai:0.4.0`, 11 Builder methods, `DEFAULT_MIN_CONFIDENCE = 0.6` |
| `jackson3-ptv` | Stale knowledge: Jackson 3 moved to groupId `tools.jackson.core`. Compare the Builder API between 3.x and 2.x, and a security-related behavior change | 3.2.3 vs 2.22.3; `allowSubTypesWithExplicitDeserializer()` exists only in 3.x; `allowIfSubTypeIsArray()` changed in 3.1.4 / 2.18.8 (databind#5981) |
| `spring-ai-hostedtool` | The class exists only in a milestone: the latest stable release doesn't have it | Not in stable 2.0.1; in the 2.1.0-M1 milestone `HostedTool` permits WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw. Either "not in 2.0.1, see 2.1.0-M1" or "2.1.0-M1 (a milestone)" counts as correct |
| `agentutils-shell-trap` | A default that's easy to miss, plus a method that doesn't exist | 0.13.0; `/bin/bash -c`; `shellCommand(...)`; `cleanEnvironment` defaults to false; there is no `JevJudge.Builder.rubric()` |

The reference answers were checked against the published javadoc and sources jars. Each task
pins the versions its reference depends on: every eval run logs a `STALE REFERENCE` warning when Maven Central has
moved past them, and `./gradlew test -Plive --tests '*ReferenceFreshness*'` fails. Re-verify the reference, then
update the pin in `Tasks.kt`.

## Running

Setup:

1. [Create a Bedrock API key](https://us-east-1.console.aws.amazon.com/bedrock/home?region=us-east-1#/api-keys/long-term/create) and `export AWS_BEARER_TOKEN_BEDROCK=...`
2. `export TYPESAFE_API_KEY=...` for the Jev judge and the `mcp-toolsearch-jev` arm.
3. Optional: `export BRAVE_API_KEY=...` for `web-brave`, and `export BEDROCK_WEB_SEARCH_MODEL=openai.gpt-5.6-terra` for `web-bedrock`.
4. Build the shell sandbox image (needs Docker): `docker build -t exquisite-evals-sandbox:1 sandbox`

The `shell` and web arms run the model's commands in a fresh container per run (`DockerCliExecBackend` from
`spring-ai-agent-utils-docker-cli`), built from `sandbox/Dockerfile`: a JDK plus `curl`, `unzip`, `jq`, `git` and
`python3`, running as a non-root user. Only the run's scratch directory is mounted, at `/workspace`, and none of the
host's environment is passed in, so the commands can't read the API keys. The network is open, since the agent has
to reach Maven Central. `EVALS_SANDBOX=local` runs commands on the host instead (with only `PATH` and `HOME` from its
environment).

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
| `AGENT_API` | `chat-completions` | `responses` for models served over the Responses API; most Bedrock mantle models only support Chat Completions |
| `JUDGE_MODEL` | `deepseek.v3.2` | The LLM judge, always over Chat Completions; keep it a different family from the agent |
| `BEDROCK_MANTLE_BASE_URL` | `https://bedrock-mantle.us-east-1.api.aws/v1` | The GPT‑5.x models use `/openai/v1` |

Embeddings for `mcp-toolsearch-vector` come from the Bedrock runtime (`InvokeModel`) with the same API key,
because mantle has no embedding models (`evals.embedding.model`, `evals.embedding.base-url`); they count as in-tool
tokens.

Run budget (`evals.budget.*`), so an agent stuck in a loop still produces a gradable answer:

| Setting | Default | What happens when it's reached |
|---|---|---|
| `max-tool-calls` | 25 | Further calls to the arm's tools return an error to the model instead of running (`toolSearchTool` isn't counted) |
| `max-model-calls` | 30 | The tools are removed and the model is told to answer with what it has found (marked `(budget)` in the report) |
| `max-input-tokens` | 250,000 | Same as `max-model-calls` (every turn re-sends the whole history) |
| hard stop | max-model-calls + 3 | The run is stopped and recorded as an error |

Garbled tool names (some models leak `<|channel|>` markers into them) are resolved to the real tool, unknown tools get an
error the model can recover from, and tool calls with cut-off JSON arguments (Kimi) get an error result and are
repaired in the history, which Bedrock would otherwise reject on the next request.

Tests:

- `./gradlew build` runs offline: no network, no cost.
- `./gradlew test -Plive --tests '*SpikeTest*'` runs live checks of each building block.
- `./gradlew test -Plive --tests '*ReferenceFreshness*'` checks the reference answers are still current.
- `./gradlew test -Plive --tests '*SandboxLiveTest*'` checks the sandbox: tools present, network up, no host keys or files, container removed.

### Watching a run in the Spring AI Inspector

The Spring AI Inspector (in the voxxeddays2026-demo repository, `spring-ai-inspector`) shows each call live: the prompt
the app sent, the prompt after the advisors (including the budget's wrap-up message), every HTTP round-trip to
Bedrock mantle and TypeSafe (Jev's questions and answers, also for the `mcp-toolsearch-jev` index), and every tool
execution. Build with `-Pinspector` to add its starter; without that flag nothing changes.

```
(cd ../voxxeddays2026-demo/spring-ai-inspector && mvn install)                 # once: the starter, into ~/.m2
java -jar ../voxxeddays2026-demo/spring-ai-inspector/spring-ai-inspector-server/target/spring-ai-inspector-server-*.jar
open http://localhost:9001
./gradlew bootRun -Pinspector -PevalTasks=jevjudge-gav -PevalArms=mcp,mcp-toolsearch-jev
```

`spring.ai.inspector.route.openai=always` (in `application.properties`) sends the mantle traffic of the agent and
the LLM judge through the inspector's proxy. The whole eval JVM is one inspector run, and the inspector keeps every
request and response body in memory, so narrow the run to a few tasks and arms. Use **Export** and **▶ Replay** to
show a recorded run without calling a model.

## Judges

| Judge | How it grades | Pass when |
|---|---|---|
| Code checks | Regexes for the required facts; invented names in known families (e.g. any `Jev*` type that doesn't exist); trap phrasing | Every fact present, no invented names |
| LLM judge | One chat call returning `factually_consistent`, `completeness` (0–4), `hallucinations[]`, `trap_handled` | Consistent, completeness ≥ 3, no hallucinations, trap handled, and the code checks pass |
| Jev judge | One `JevJudge` call: `grounded` noul ≥ 0.6, `completeness` score ≥ 2, `trap_handled` noul ≥ 0.7; the code checks as local criteria | Every criterion passes |

Both judges fail an answer that fails the code checks, so their verdicts are comparable.

Baseline, 16 answers:

| | LLM judge (`deepseek.v3.2`) | Jev |
|---|---|---|
| Passed | 4/16 | 4/16 |
| Same verdict | 16/16 | 16/16 |
| Time for all answers | 42 s | 5.4 s |
| Input / output tokens | 18.7k / 1.9k | 41.5k / 700 |
| Cost at list prices | $0.0152 | $0.0017 |

- **Both judges caught every wrong answer,** and they agreed on all 16.
- **The LLM judge explains its verdicts but misses things.** It scored two failing answers 1.00: on
  `spring-ai-hostedtool`/`mcp` it praised the answer for "properly noting the 2.1.0-M1 milestone" though it never
  says "milestone", and on `jevjudge-gav`/`web-brave` it missed that `feedbackRenderer` wasn't listed. The code checks
  caught both, which is why every verdict also requires them. It also scores an empty answer 0.67 ("nothing wrong was
  said").
- **Jev's `grounded` bar is 0.6.** At 0.7 it failed correct answers that added detail the reference doesn't mention.
- **Jev was 8× faster and 9× cheaper.** One Jev call answers every criterion together, while the LLM judge writes an
  explanation for each answer. Jev sends more than twice the input tokens (its state carries the question, answer,
  reference and tool-call trace), but its input is 15× cheaper per token and its output isn't billed.

List prices per 1M tokens, checked 2026-10-06; check the providers' pricing pages before relying on them:

| Model | Input | Output | Source |
|---|---|---|---|
| Jev (TypeSafe) | $0.042 | $0 (not billed) | [typesafe.ai](https://typesafe.ai/) ("$42 per billion input tokens"); output: [Cloudflare's Jev model page](https://developers.cloudflare.com/ai/models/typesafe/jev/) |
| `deepseek.v3.2` on Bedrock | $0.62 | $1.85 | [Amazon Bedrock pricing](https://aws.amazon.com/bedrock/pricing/); US-region prices per third-party trackers ([computeprices.com](https://computeprices.com/providers/aws/models/deepseek-v3-2)) |

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
| `ToolSearchDiagnosticsTest.kt` | Live diagnostics for the tool-search arms: tool definition sizes, index rankings, replay of recorded queries |

## Caveats

- Every result here is from one trial per task × arm; model output varies between runs, so treat them as indicative.
- The reference answers were checked through javadocs.dev and the sources jars, and javadocs.dev is also what the MCP
  arms use. The facts came from the published artifacts, not from how javadocs.dev renders them.
- `web-bedrock` hasn't been measured: this account can't use the GPT‑5.x models it needs. The arm is ready and turns
  on when `BEDROCK_WEB_SEARCH_MODEL` is set.
- The shell sandbox isolates files and secrets, not the network: the agent has to reach Maven Central.

## Tool search

The `mcp-toolsearch*` arms aren't part of the baseline above. Diagnostic runs with Kimi K2.5
(`results/20261005-*-toolsearch-diag-*`, two trials of the three MCP arms) found two systemic limits at this scale:

- **Little to save.** The 8 javadocs.dev tool definitions are about 1.5k tokens per turn, at most 19% of the `mcp`
  arm's input; most input is tool results re-sent every turn. Searches return 3–5 tools and Spring AI keeps every
  tool returned, so after one search the agent holds 6–8 of the 8 anyway.
- **Keyword search strands the agent.** The model searches with the question's words ("typesafe-spring-ai JevJudge
  rubric builder Maven"), not the capability it needs. Of 23 keyword searches, 5 returned nothing and 9 at most one
  tool; in 3 of 8 runs the agent never got `search_artifacts` or `symbol_to_artifact`, and once answered "these
  libraries don't exist" after two model calls. The vector index always returns about 5 tools, so it never strands
  the agent, but saves no tokens.

Across those runs: `mcp` 5/8 passed with 713k input tokens, `mcp-toolsearch` 5/8 with 582k, `mcp-toolsearch-vector`
4/8 with 827k. Spring AI recommends tool search from about 10 tools; `ToolSearchDiagnosticsTest` prints the rankings
and replays the recorded queries.
