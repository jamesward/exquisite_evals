# Exquisite Evals

A demo of **AI evals** built with [Spring AI](https://docs.spring.io/spring-ai/reference/2.1/index.html) 2.1.0-M1.
It measures how well an AI agent can work out the API of a Java library it has never seen. Each task needs several
steps in order: find which artifact contains a class, get the Maven coordinates (the GAV), resolve the latest version,
read the class's API, and report exact facts.

The same agent model gets different tool sets (**arms**). Every answer is then graded three ways:

- **Code checks**: code verifies the required facts are present (versions, coordinates, method names, defaults) and flags invented API names.
- **LLM as judge**: a chat model compares the answer with a verified reference answer and returns a JSON verdict.
- **Jev as judge**: [`JevJudge`](https://github.com/spring-ai-community/spring-ai-typesafe) asks typed questions (a "noul" true/false score, plus scores) in one call, and adds the code checks as local criteria.

The report compares the arms on accuracy, tokens (in, out, and tokens spent inside tools), model calls, tool calls and
time. It also compares the judges on their verdicts, cost and how often they agree.

## Arms

All arms use the same agent model and the same system prompt. The agent is `moonshotai.kimi-k2.5` on Amazon
Bedrock (Chat Completions) since 2026-10-02 12:55; the earlier runs used `openai.gpt-oss-120b` via the OpenAI
Responses API. Each run records its models in `meta.json`. The prompt is a typical coding-assistant prompt: look things up instead of relying on memory, resolve
the coordinates and latest version first, and say so if something doesn't exist. Only the tools differ.

| Arm | Tools | Enabled when |
|---|---|---|
| `base` | none: the model's built-in knowledge only | always |
| `shell` | `Bash` + `TodoWrite` from [spring-ai-agent-utils](https://github.com/spring-ai-community/spring-ai-agent-utils), so it can curl, download, unzip and grep jars like a coding assistant | always |
| `web-brave` | Brave `WebSearch` + `WebFetch` (which summarizes each page with the model) + shell | `BRAVE_API_KEY` set |
| `web-bedrock` | Bedrock's server-side `web_search` (`external_web_access=false`) + shell | `BEDROCK_WEB_SEARCH_MODEL` set to a GPT‑5.x model your account can use |
| `mcp` | the [javadocs.dev](https://www.javadocs.dev/mcp) MCP tools, all offered up front | always |
| `mcp-toolsearch` | the same MCP tools behind Spring AI's `ToolSearchToolCallingAdvisor`, which shows the model tools only as it searches for them (keyword search: in-process Lucene index) | always |
| `mcp-toolsearch-vector` | the same, with semantic search: `VectorToolIndex` over an in-memory `SimpleVectorStore`, embedding tool descriptions and queries with Cohere Embed v3 (`cohere.embed-english-v3`) on the Bedrock runtime | always |

## Tasks

The reference answers were checked against the published javadoc and sources jars on 2026-09-30 (`spring-ai-hostedtool`'s
was rewritten on 2026-10-02, after javadocs.dev started resolving "latest" to the latest stable release). They need
re-checking whenever a newer "latest" version is released.

| Task | What it tests | Reference (short) |
|---|---|---|
| `jevjudge-gav` | Find the artifact from just a class name. Two artifacts contain a `JevJudge`, one of them a Scala library. Then latest version, the Builder API and a constant | `org.springaicommunity:typesafe-spring-ai:0.3.0`, 11 Builder methods, `DEFAULT_MIN_CONFIDENCE = 0.6` |
| `jackson3-ptv` | Stale knowledge: Jackson 3 moved to groupId `tools.jackson.core`. Compare the Builder API between 3.x and 2.x, and a security-related behavior change | 3.2.3 vs 2.22.3; `allowSubTypesWithExplicitDeserializer()` exists only in 3.x; `allowIfSubTypeIsArray()` changed in 3.1.4 / 2.18.8 (databind#5981) |
| `spring-ai-hostedtool` | The class exists only in a milestone: the latest stable release doesn't have it | Not in stable 2.0.1; in the 2.1.0-M1 milestone `HostedTool` permits WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw. Either "not in 2.0.1, see 2.1.0-M1" or "2.1.0-M1 (a milestone)" counts as correct |
| `agentutils-shell-trap` | A default that's easy to miss, plus a method that doesn't exist | 0.12.0; `/bin/bash -c`; `shellCommand(...)`; `cleanEnvironment` defaults to false; there is no `JevJudge.Builder.rubric()` |

## Running

Setup:

1. [Create a Bedrock API key](https://us-east-1.console.aws.amazon.com/bedrock/home?region=us-east-1#/api-keys/long-term/create) and `export AWS_BEARER_TOKEN_BEDROCK=...`
2. `export TYPESAFE_API_KEY=...` for the Jev judge.
3. Optional: `export BRAVE_API_KEY=...` for `web-brave`, and `export BEDROCK_WEB_SEARCH_MODEL=openai.gpt-5.6-terra` for `web-bedrock`.

Run (needs Java 21+):

```
./gradlew bootRun                                                          # every task x every enabled arm
./gradlew bootRun -PevalTasks=jevjudge-gav,jackson3-ptv -PevalArms=mcp,mcp-toolsearch
./gradlew bootRun --args='--evals.budget.max-tool-calls=15'                # tighter budget
```

Results print to the console and are also written to `results/<timestamp>[-<label>]/`: `report.md`,
`results.json` (full answers, every tool call, per-judge criteria) and `summary.csv` (one row per task × arm).
`results/` is outside `build/`, so `./gradlew clean` keeps past runs. Label a run with `-PevalLabel=...`, and
compare runs with `python3 compare.py BEFORE_DIR[,DIR...] AFTER_DIR[,DIR...]`.

Run budget (`evals.budget.*`), so an agent stuck in a loop still produces a gradable answer:

| Setting | Default | What happens when it's reached |
|---|---|---|
| `max-tool-calls` | 25 | Further tool calls return an error to the model instead of running |
| `max-model-calls` | 30 | The tools are removed and the model is told to answer with what it has found (marked `(budget)` in the report) |
| `max-input-tokens` | 250,000 | Same as `max-model-calls` (every turn re-sends the whole history) |
| hard stop | max-model-calls + 3 | The run is stopped and recorded as an error |

Embeddings for `mcp-toolsearch-vector` come from the Bedrock runtime (`InvokeModel`) with the same Bedrock API key,
because Bedrock mantle has no embedding models (`evals.embedding.model` / `evals.embedding.base-url`). Embedding tokens
are counted as in-tool tokens.

Model settings:

| Env var | Default | What it sets |
|---|---|---|
| `AGENT_MODEL` | `moonshotai.kimi-k2.5` | The agent, for every arm (also WebFetch's page summaries) |
| `AGENT_API` | `chat-completions` | `responses` for gpt-oss (how the earlier runs were made); most mantle models only support Chat Completions |
| `JUDGE_MODEL` | `deepseek.v3.2` | The LLM judge, always over Chat Completions; keep it a different family from the agent |
| `BEDROCK_MANTLE_BASE_URL` | `https://bedrock-mantle.us-east-1.api.aws/v1` | The GPT‑5.x models use `/openai/v1` |

Reproduce the earlier gpt-oss runs with `AGENT_API=responses AGENT_MODEL=openai.gpt-oss-120b JUDGE_MODEL=...`
(they used `openai.gpt-oss-120b` as the LLM judge too).

Tests:

- `./gradlew build` runs offline: no network, no cost.
- `./gradlew test -Plive --tests '*SpikeTest*'` runs the live checks of each building block.

## Summary of results

These come from one trial per task × arm, run on 2026-09-30 (`results/20260930-133320-pre-mcp-changes`), so treat them as
indicative only.

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Avg time |
|---|---|---|---|---|---|
| base | 0/4 | 0/4 | 0/4 | 10k | 28 s |
| shell | 0/4 | 0/4 | 0/4 | 643k | 52 s |
| web-brave | 1/4 | 1/4 | 0/4 | 903k (incl. 99k inside WebFetch) | 75 s |
| **mcp** | **3/4** | **3/4** | **2/4** | 664k | 43 s |
| mcp-toolsearch | 1/4 | 1/4 | 0/4 | 305k | 32 s |

- **javadocs.dev MCP is the only arm that reliably got the answers right.** It passed three of the four tasks under
  both the code checks and the LLM judge. It was the only arm to solve `jevjudge-gav`: `symbol_to_artifact` answered
  "which artifact has this class?" in one call.
- **The base model makes things up confidently.** It gave invented coordinates (`com.github.patrickfav:jev-judge:1.4.0`),
  18 invented Jackson Builder methods, and described how to use the non-existent `rubric()` method.
- **Shell and web access are expensive and still often wrong.** The shell arm concluded that `JevJudge` doesn't exist
  on Maven Central. The Brave arm found the correct Builder methods but gave the wrong artifact
  (`typesafe-java-sdk`). Both spent hundreds of thousands of tokens.
- **Tool search cost fewer tokens but was less reliable.** It used about half the tokens of `mcp`, but gave up on
  `jevjudge-gav` and returned an empty answer on `jackson3-ptv`. The MCP server only has 8 tools, which is below the
  roughly 10 where Spring AI recommends tool search. The extra search step adds turns and room for mistakes, and here
  it bought little.
- **Semantic tool search (Cohere v3) did as well as offering every tool up front,** in a separate run (see
  [Tool search: keyword vs embeddings](#tool-search-keyword-vs-embeddings)). Where the keyword index gave up on
  `jevjudge-gav`, the vector index led straight to `symbol_to_artifact`.
- **`jackson3-ptv` was the hardest task:** no arm passed it. Every tool-using arm kept searching the old
  `com.fasterxml` groupId. Two arms (`shell`, `mcp`) hit the input-token budget and were made to answer.
- **The judges agreed on 17 of 20 answers.** Jev was stricter: in all three disagreements, Jev's `grounded` score
  (0.52–0.65) was just under its 0.7 pass bar while the LLM judge passed the answer.
- **Jev was about 5× cheaper and 21× faster than the LLM judge.** Grading all 20 answers cost about $0.0021 with Jev
  and $0.0108 with the LLM judge, even though the LLM judge here is `gpt-oss-120b`, one of the cheapest models on
  Bedrock. Jev doesn't charge for output tokens, and its input tokens cost less than a third of `gpt-oss-120b`'s.
  See [Judge cost](#judge-cost).

## After the javadocs.dev changes (2026-10-02)

The full matrix was re-run against the deployed javadocs.dev changes (`results/20261002-112143-post-mcp-changes`),
and `spring-ai-hostedtool` was then re-run on all arms with its updated reference
(`results/20261002-114020-post-mcp-changes-hostedtool-ref`). The full comparison is in
[`results/COMPARISON-20261002.md`](results/COMPARISON-20261002.md). One trial per side, so treat it as indicative.

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens |
|---|---|---|---|---|
| base | 0/4 → 0/4 | 0/4 → 0/4 | 0/4 → 0/4 | 10k → 10k |
| shell | 0/4 → 0/4 | 0/4 → 0/4 | 0/4 → 0/4 | 643k → 905k |
| web-brave | 1/4 → 0/4 | 1/4 → 0/4 | 0/4 → 0/4 | 903k → 882k |
| mcp | 3/4 → 2/4 | 3/4 → 2/4 | 2/4 → 2/4 | 664k → 688k |
| mcp-toolsearch | 1/4 → **3/4** | 1/4 → 2/4 | 0/4 → 1/4 | 305k → 385k |
| mcp-toolsearch-vector | 3/4 → 0/4 | 3/4 → 0/4 | 2/4 → 0/4 | 674k → 491k |

- **Keyword tool search improved the most.** It now solves `jevjudge-gav` and `agentutils-shell-trap`, and it got
  all 10 facts on `jackson3-ptv` for the first time (both judges still failed it: it claimed the 2.x Builder has the
  same methods).
- **Agents used the new options.** `list_javadoc_symbols` `filter` was used in every MCP run; `includePreReleases`
  was used on `jackson3-ptv`.
- **`spring-ai-hostedtool` is now the hardest task: every arm failed it.** `get_latest_version` returns 2.0.1 by
  default, which has no `HostedTool`. On the re-run with the updated reference, all three MCP arms looked in 2.0.1
  first; two later called `get_latest_version` with `includePreReleases: true` but still answered from 2.0.1 or
  invented types (`HostedTool.Function`, `HostedTool.RawJson`), and the `mcp` arm said the class isn't in 2.1.0-M1.
  `web-brave` found all six types in the source but presented 2.1.0-M1 as a plain release, missing the
  milestone fact.
- **The updated reference grades correctly.** An earlier vector-arm answer ("not in the stable 2.0.1, only in the
  2.1.0-M1 milestone", plus all six types) passes the code checks and both live judges under the new reference
  (`SpikeTest` 9). Under the old reference, both judges failed it.
- **The vector arm's regressions are model variance, not search.** On `jevjudge-gav`, `symbol_to_artifact`
  returned `typesafe-spring-ai` three times and the model still concluded "no artifact contains JevJudge" (the
  keyword arm did the same thing in the baseline). Several trials per cell are needed to tell real changes from noise.
- **Harness bug found:** the tool-call cap returned plain text to `toolSearchTool`, and the tool-search advisor
  crashed parsing it as JSON. The cap now applies only to the arm's own tools.
- **Judges agreed on 23 of 24 answers.** Jev took 7 s for all 24, the LLM judge 96 s.

## Kimi K2.5 as the agent, DeepSeek V3.2 as the judge (2026-10-02)

Same tasks, same deployed javadocs.dev, new agent model (`moonshotai.kimi-k2.5`) and a different-family LLM judge
(`deepseek.v3.2`): `results/20261002-125459-kimi-k2.5-judge-deepseek-v3.2`. Compared with the post-change gpt-oss runs
in [`results/COMPARISON-models-20261002.md`](results/COMPARISON-models-20261002.md). One trial each.

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Avg time |
|---|---|---|---|---|---|
| base | 0/4 → 0/4 | 0/4 → 0/4 | 0/4 → 0/4 | 10k → 3k | 16 → 6 s |
| shell | 0/4 → 0/4 | 0/4 → 0/4 | 0/4 → 0/4 | 905k → 798k | 126 → 307 s |
| web-brave | 0/4 → 1/4 | 0/4 → 1/4 | 0/4 → 0/4 | 882k → 842k | 76 → 68 s |
| mcp | 2/4 → 3/4 | 2/4 → 2/4 | 2/4 → 2/4 | 688k → 484k | 62 → 27 s |
| mcp-toolsearch | 3/4 → 2/4 | 2/4 → 2/4 | 1/4 → 2/4 | 385k → 184k | 42 → 16 s |
| mcp-toolsearch-vector | 0/4 → 2/4 | 0/4 → 2/4 | 0/4 → 1/4 | 491k → 440k | 50 → 45 s |

- **`jackson3-ptv` was passed for the first time,** by Kimi on `mcp` (10/10 facts, both judges). The vector arm
  also described the API diff and the 3.1.4 / 2.18.8 change correctly, but never wrote the `tools.jackson.core`
  groupId, so the code checks failed it.
- **All three MCP arms solved `jevjudge-gav`.** With gpt-oss, the vector arm had concluded the class didn't exist.
- **Kimi is cheaper and faster with the MCP tools:** about 30–50% fewer tokens and roughly half the time, with
  fewer tool calls. The shell arm got slower (307 s average).
- **`spring-ai-hostedtool` is still hard:** only `mcp-toolsearch` and `web-brave` got all 8 facts. The other MCP
  arms stopped at "not in 2.0.1".
- **The judges catch what the code checks can't.** On `agentutils-shell-trap/mcp`, Kimi mentioned every required
  fact but said child processes *don't* inherit the environment (they do). The code checks passed it; both judges
  failed it.
- **The new LLM judge is much cheaper to run:** DeepSeek V3.2 used 3.0k output tokens for 24 answers, against 13.8k
  for gpt-oss-120b. It agreed with Jev on 22 of 24 answers.
- **Known issue:** one run (`spring-ai-hostedtool/shell`) ended with a Bedrock 400 error. Kimi emitted a `Bash` call
  with truncated JSON arguments, and replaying that tool call in the next request was rejected.

## Detailed results

### Every run

Column meanings:

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

### By task

**`jevjudge-gav`: find the artifact from a class name.**

- `mcp` solved it in 11 model calls. It used `symbol_to_artifact` → `get_latest_version` → `list_javadoc_symbols` →
  `get_javadoc_symbol`, and got every one of the 15 facts right.
- `web-brave` got 14 of 15 facts, the complete Builder API and the 0.6 default, but gave the SDK artifact
  (`typesafe-java-sdk`) instead of `typesafe-spring-ai`. Both judges failed it for that. Jev's `grounded` score was 0.03.
- `shell` searched Maven Central by class name, found nothing, and concluded the class doesn't exist.
  `mcp-toolsearch` concluded the same, even though it called `symbol_to_artifact`.
- `base` invented a different library: `com.github.patrickfav:jev-judge:1.4.0`, with builder methods like
  `maxConfidence` and `strictMode`.

**`jackson3-ptv`: Jackson 3 vs 2.**

- No arm passed. The tool-using arms mostly kept searching `com.fasterxml.jackson.core` for a 3.x release, which is
  the stale knowledge this task is designed to expose.
- `mcp` and `shell` each spent more than 250k input tokens before being made to answer.
- `mcp` then claimed the 3.x and 2.x Builder APIs are identical, missing
  `allowSubTypesWithExplicitDeserializer()`.
- `web-brave` invented `allowIfSubTypeIsCollection` / `allowIfSubTypeIsMap` and gave the wrong fix versions
  (3.1.3 / 2.18.0).
- `mcp-toolsearch` returned an empty answer after 6 calls. The LLM judge scored it 0.67 because "nothing stated"
  counts as not wrong; the code checks (0/10) and the overall fail verdict caught it.
- `base` invented 18 methods (`allowIfBaseTypeIsEnum`, `allowIfSubTypeIsInterface`, …).

**`spring-ai-hostedtool`: the milestone release.**

- Both MCP arms listed all six records and `Raw`, and correctly said the latest release is 2.1.0-M1. The LLM judge
  passed both. Jev's `grounded` score was 0.65 and 0.62, just under the bar, because the answers include extra
  (correct) detail that the reference doesn't mention.
- `shell` listed all six records correctly, but attributed them to the wrong version (`1.0.0-RC1`).
- `web-brave` relied on a blog-style source and gave wrong type names (`WebSearchTool`, `RemoteMcpTool`), missing `Raw`.
- `base` invented `RestTool` / `JavaMethodTool` / `RawTool`.

**`agentutils-shell-trap`: a buried default plus a method that doesn't exist.**

- `mcp` (answered under the budget) and `web-brave` got everything right, including saying that `rubric()` doesn't
  exist. `mcp` also passed Jev. `web-brave` failed Jev's `grounded` check at 0.52.
- `mcp-toolsearch` answered correctly but didn't state the version, so the code checks failed it. The run's trap
  check also missed its phrasing "does not contain a `rubric()` method". That pattern has since been added to the
  code check, but the verdict in the table is from before the fix.
- `shell` and `base` both fell for the trap: they described how to use `rubric()` and gave the default shell as
  `/bin/sh`.

### Tool search: keyword vs embeddings

`mcp-toolsearch-vector` was added after the main run and measured on its own, on 2026-09-30
(`results/20260930-142417-pre-mcp-changes-vector`). It's a different run, so compare the two rows loosely:

| Task | Arm | Checks | LLM | Jev | In tok | Out tok | In-tool tok | Model calls | Tool calls | Time s |
|---|---|---|---|---|---|---|---|---|---|---|
| jevjudge-gav | mcp-toolsearch-vector | 15/15 | PASS 1.00 | PASS 0.93 | 31,703 | 1,751 | 907 | 8 | 5 | 20.9 |
| jackson3-ptv | mcp-toolsearch-vector | 4/10 +2 invented (budget) | FAIL 0.00 | FAIL 0.11 | 302,435 | 5,908 | 901 | 20 | 18 | 71.9 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 7/7 | PASS 1.00 | FAIL 0.70 | 50,980 | 1,611 | 900 | 10 | 8 | 21.7 |
| agentutils-shell-trap | mcp-toolsearch-vector | 5/5 (budget) | PASS 1.00 | PASS 0.85 | 270,745 | 4,944 | 913 | 25 | 22 | 65.4 |

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Avg time |
|---|---|---|---|---|---|
| mcp (main run) | 3/4 | 3/4 | 2/4 | 664k | 43 s |
| mcp-toolsearch, Lucene (main run) | 1/4 | 1/4 | 0/4 | 305k | 32 s |
| mcp-toolsearch-vector, Cohere v3 | 3/4 | 3/4 | 2/4 | 674k | 45 s |

- **Same accuracy as offering all tools, at about the same cost.** The embedding calls themselves are tiny: about 900
  tokens per task, for indexing 8 tool descriptions and embedding each search query.
- **It found the key tool.** On `jevjudge-gav` and `spring-ai-hostedtool` the model's first real call was
  `symbol_to_artifact`, then `get_latest_version` → `list_javadoc_symbols` → `get_javadoc_symbol`.
- **The embeddings separate these tools only weakly.** In a live check (`SpikeTest` 8), the query "which Maven
  artifact contains this Java class?" ranked `symbol_to_artifact` 4th, with all 8 tools scoring 0.49–0.59, because
  every description talks about Maven artifacts and javadoc. With `maxResults(5)`, it still got through.
- **`jackson3-ptv` still failed,** stuck on the old `com.fasterxml` groupId like every other arm, and invented
  `allowIfSubTypeMatches`.

### By judge

| Judge | Pass rate | In tok | Out tok | Total time | Cost (all 20) | Errors |
|---|---|---|---|---|---|---|
| LLM (gpt-oss-120b) | 5/20 | 26,664 | 11,422 | 122.3 s | $0.0108 | 0 |
| Jev (TypeSafe System One) | 2/20 | 49,928 | 875 (not billed) | 5.8 s | $0.0021 | 0 |

How often each pair agreed:

| Comparison | Agreement |
|---|---|
| Overall verdict, LLM vs Jev | 17/20 |
| Model-judged criteria only, LLM vs Jev | 16/20 |
| LLM model-judged criteria vs code checks | 19/20 |
| Jev model-judged criteria vs code checks | 17/20 |

What stood out about each judge:

- **Both judges catch hallucinations.** Every answer with invented APIs or wrong versions failed both.
  - The LLM judge also lists the specific wrong claims, e.g. "Incorrect Maven coordinates:
    org.springaicommunity:typesafe-java-sdk:0.3.0".
  - Jev's feedback is built from the rubric, e.g. "grounded: … scored 0.03, needs at least 0.70", so it's
    consistent from run to run but less specific.
- **Jev is stricter on "grounded".** It fails correct answers that add details the reference doesn't mention. A
  threshold around 0.6 would match the LLM judge on these runs; whether to lower it is a design decision, not a bug.
- **The LLM judge doesn't penalize empty answers enough.** It gave the empty `jackson3-ptv/mcp-toolsearch` answer a
  0.67 score. That's why every verdict also requires the code checks to pass.
- **Cost and speed:** Jev was about 5× cheaper and 21× faster (see [Judge cost](#judge-cost)). One Jev call answers
  every criterion together, while the LLM judge generates a written explanation for each answer.

#### Judge cost

List prices on 2026-09-30, per 1M tokens:

| Model | Input | Output | Source |
|---|---|---|---|
| Jev (TypeSafe) | $0.042 | $0 (not billed; cached input also $0) | Input: [typesafe.ai](https://typesafe.ai/) ("$42 per billion input tokens"). Output and cached input: [Cloudflare's Jev model page](https://developers.cloudflare.com/ai/models/typesafe/jev/) |
| `gpt-oss-120b` on Bedrock (Standard tier) | $0.15 | $0.60 | [Amazon Bedrock pricing](https://aws.amazon.com/bedrock/pricing/); US regions per third-party price trackers (the AWS page lists Sydney at $0.1545 / $0.618) |

What this run's usage costs at those prices:

| Judge | Input cost | Output cost | Total | Per answer |
|---|---|---|---|---|
| LLM (gpt-oss-120b) | 26,664 tok → $0.0040 | 11,422 tok → $0.0069 | **$0.0108** | $0.00054 |
| Jev | 49,928 tok → $0.0021 | 875 tok → $0 | **$0.0021** | $0.00011 |

- **Input is cheaper per token:** Jev's input costs $0.042 per 1M tokens against $0.15 for `gpt-oss-120b`, about 3.6× cheaper.
- **Output is free:** that's 63% of the LLM judge's bill that Jev doesn't pay.
- **Jev sends more input:** about 1.9× as many input tokens, because its state carries the question, answer,
  reference and a trace of the tool calls. That's why the total gap is about 5× rather than larger.
- **The gap grows with a stronger judge model.** `gpt-oss-120b` is one of the cheapest models on Bedrock. A frontier
  model as the LLM judge would cost many times more per token, while Jev's cost wouldn't change.
- These are one run's numbers at list prices. Check the providers' pricing pages before relying on them.

## How it works (code map)

| File | What it does |
|---|---|
| `Tasks.kt` | Task definitions: prompt, frozen reference answer, required facts, allowed API names, trap flag |
| `CodeChecks.kt` | Deterministic checks for facts and invented names (normalizes typographic hyphens and spaces) |
| `Arms.kt` | `ArmCatalog`: the tool configuration of each arm; optional arms are enabled from config |
| `WebTools.kt` | Brave search + fetch; Bedrock hosted `web_search` |
| `Embeddings.kt` | Cohere Embed v3 on the Bedrock runtime (document vs query input types, token accounting) |
| `Judges.kt` | `LlmJudge` and `JevAsJudge` (both include the code checks) |
| `RunBudget.kt` | Tool-call cap, recovery from garbled tool names, handling of unknown tools |
| `TokenTracking.kt` | Per-turn token and tool accounting; forces the wrap-up answer when the budget is used up |
| `EvalRunner.kt` | Runs the task × arm matrix, calls both judges, writes the report |

## Caveats

- Everything above is from one trial per task and arm; model output varies between runs, so treat these as indicative.
- The reference answers were checked through javadocs.dev and the sources jars, and javadocs.dev is also what the MCP
  arms use. The facts came from the published artifacts, not from how javadocs.dev renders them.
- `web-bedrock` hasn't been measured: this account can't use the GPT‑5.x models it needs. The arm is ready and turns
  on when `BEDROCK_WEB_SEARCH_MODEL` is set.
- The shell tool runs commands the model writes, directly on the host, with only a scratch working directory. Use the
  agent-utils Docker backend for real isolation.
