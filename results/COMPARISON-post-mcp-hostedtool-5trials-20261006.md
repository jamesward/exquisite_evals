# `spring-ai-hostedtool` / `mcp`: five post-change trials (2026-10-06)

Same task, `mcp` arm, agent `moonshotai.kimi-k2.5`, LLM judge `deepseek.v3.2`, Jev, budgets and updated javadocs.dev server. Every trial used `-Pinspector`. All result directories are retained.

| Trial | Result directory | Facts | Checks / LLM / Jev | Total tokens | Model / tool calls | Time | Budget |
|---|---|---:|---|---:|---:|---:|---|
| 1 | `20261006-090923-post-mcp-hostedtool-trial-1` | 1/8 | ... | 312,302 | 18 / 17 | 94.8 s | input-token limit |
| **2 (best)** | `20261006-091043-post-mcp-hostedtool-trial-2` | **8/8** | **PP.** | **217,066** | **24 / 23** | **71.7 s** | no |
| 3 | `20261006-091220-post-mcp-hostedtool-trial-3` | 2/8 | ... | 241,794 | 27 / 25 | 84.0 s | no |
| 4 | `20261006-091339-post-mcp-hostedtool-trial-4` | 2/8 | ... | 334,978 | 20 / 19 | 69.3 s | input-token limit |
| 5 | `20261006-091457-post-mcp-hostedtool-trial-5` | 6/8 | ... | 458,627 | 13 / 12 | 69.4 s | input-token limit |

Across five trials: code checks 1/5, LLM judge 1/5, Jev 0/5; median 312,302 total tokens (range 217,066–458,627). No trial passed all three graders.

## Best trial

Trial 2 is best by accuracy first, then token efficiency. It:

1. resolved stable `org.springframework.ai:spring-ai-openai` to `2.0.1` and confirmed `HostedTool` was absent;
2. eventually called `get_latest_version` for that same artifact with `includePreReleases=true`, getting `2.1.0-M1`;
3. listed and read the 2.1.0-M1 sources;
4. reported all six permitted types and identified `HostedTool.Raw` as the raw-JSON option, explicitly calling 2.1.0-M1 a milestone.

Code checks found 8/8 facts and DeepSeek found no factual errors, scoring completeness 4/4. Jev alone failed: `grounded=0.59` against the configured 0.60 threshold (completeness passed at 0.90). This is a threshold-boundary judge disagreement, so the trial is **PP.**, not a unanimous pass.

## What varied

- Only trials 2 and 3 requested pre-releases for the correct `spring-ai-openai` artifact. Trial 1 used `includePreReleases=true` on the unrelated `com.openai:openai-java` artifact; trials 4 and 5 never used it.
- Trial 3 discovered `2.1.0-M1` with tool call 25, but did not inspect that version afterward and omitted all six types.
- Trial 2 still wandered through unrelated artifacts before making the right prerelease call; the successful path took 23 tool calls and 217k tokens.
- Selecting trial 2 alone is optimistic. For aggregate before/after comparisons, keep the original single post-change matrix result; if trial 2 appears in slides, label it **best of 5** and show the 1/5 completion rate.

## Old-server replication

After rolling javadocs.dev back, one additional trial (`results/20261006-092603-old-mcp-hostedtool-extra-trial`) reproduced the original old-server result almost exactly:

| Old-server trial | Facts | Checks / LLM / Jev | Total tokens | Model / tool calls | Time |
|---|---:|---|---:|---:|---:|
| Original matched baseline | 7/8 | ... | 23,810 | 6 / 5 | 11.2 s |
| Extra replication | 7/8 | ... | 23,978 | 6 / 5 | 13.9 s |

Both old-server answers found all six `HostedTool` types quickly and failed only the version-context fact: they presented `2.1.0-M1` as the latest release without identifying it as a milestone or contrasting it with stable 2.0.1. The extra answer literally says “2.1.0-M1 (the latest release).” The LLM judge nevertheless gave its semantic answer 1.00 and incorrectly said it identified the milestone; the deterministic fact check prevented the final pass.

This makes the tradeoff clearer: the old behavior was fast and reliably exposed the requested milestone API, but mislabeled a prerelease as “latest.” The new behavior is semantically correct about stable latest, but makes this task require an extra reasoning step that Kimi chose in only 1/5 trials.
