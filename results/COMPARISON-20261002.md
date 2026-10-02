# Before vs after the javadocs.dev MCP changes

- **Before:** `20260930-133320-pre-mcp-changes` (main run, summary reconstructed) + `20260930-142417-pre-mcp-changes-vector`
- **After:** `20261002-112143-post-mcp-changes` (all 4 tasks × 6 arms)
  + `20261002-112526-post-mcp-changes-rerun` (`spring-ai-hostedtool / mcp-toolsearch-vector`, re-run after a harness crash)
  + `20261002-114020-post-mcp-changes-hostedtool-ref` (`spring-ai-hostedtool` on all 6 arms, graded with the updated reference)

Later directories override earlier ones, so every `spring-ai-hostedtool` "after" cell comes from the last run.
`spring-ai-hostedtool` is graded against different references on the two sides: "before" against the original one
(2.1.0-M1 is "latest"), which matched javadocs.dev's behavior at the time; "after" against the 2026-10-02 reference
(not in the latest stable 2.0.1, only in the 2.1.0-M1 milestone). One trial per task × arm on each side, so single
cells are noisy.

```
python3 compare.py results/20260930-133320-pre-mcp-changes,results/20260930-142417-pre-mcp-changes-vector \
                   results/20261002-112143-post-mcp-changes,results/20261002-112526-post-mcp-changes-rerun,results/20261002-114020-post-mcp-changes-hostedtool-ref
```

## By arm (before -> after)

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Tool calls | Avg time s |
|---|---|---|---|---|---|---|
| base | 0/4 -> 0/4 | 0/4 -> 0/4 | 0/4 -> 0/4 | 10k -> 10k | 0 -> 0 | 28 -> 16 |
| mcp | 3/4 -> 2/4 | 3/4 -> 2/4 | 2/4 -> 2/4 | 664k -> 688k | 56 -> 97 | 43 -> 62 |
| mcp-toolsearch | 1/4 -> 3/4 | 1/4 -> 2/4 | 0/4 -> 1/4 | 305k -> 385k | 36 -> 57 | 32 -> 42 |
| mcp-toolsearch-vector | 3/4 -> 0/4 | 3/4 -> 0/4 | 2/4 -> 0/4 | 674k -> 491k | 53 -> 77 | 45 -> 50 |
| shell | 0/4 -> 0/4 | 0/4 -> 0/4 | 0/4 -> 0/4 | 643k -> 905k | 73 -> 73 | 52 -> 126 |
| web-brave | 1/4 -> 0/4 | 1/4 -> 0/4 | 0/4 -> 0/4 | 903k -> 882k | 86 -> 87 | 75 -> 76 |

## By task x arm (verdicts = code checks / LLM / Jev, P = pass)

| Task | Arm | Facts | Verdicts | Total tokens | Tool calls | Time s |
|---|---|---|---|---|---|---|
| agentutils-shell-trap | base | 2/5 +1 inv -> 1/5 +2 inv | ... -> ... | 2k -> 3k | 0 -> 0 | 35.8 -> 15.1 |
| agentutils-shell-trap | mcp | 5/5 (budget) -> 5/5 (budget) | PPP -> PPP | 287k -> 279k | 19 -> 24 | 64.5 -> 71.0 |
| agentutils-shell-trap | mcp-toolsearch | 3/5 -> 5/5 | ... -> PP. | 244k -> 108k | 18 -> 16 | 56.7 -> 59.2 |
| agentutils-shell-trap | mcp-toolsearch-vector | 5/5 (budget) -> 0/5 | PPP -> ... | 277k -> 187k | 22 -> 19 | 65.4 -> 52.2 |
| agentutils-shell-trap | shell | 1/5 -> 1/5 +1 inv (budget) | ... -> ... | 154k -> 286k | 25 -> 25 | 54.6 -> 209.1 |
| agentutils-shell-trap | web-brave | 5/5 -> 3/5 +1 inv | PP. -> ... | 270k -> 248k | 25 -> 25 | 91.8 -> 91.7 |
| jackson3-ptv | base | 3/10 +18 inv -> 3/10 +11 inv | ... -> ... | 4k -> 4k | 0 -> 0 | 29.8 -> 21.8 |
| jackson3-ptv | mcp | 6/10 (budget) -> 6/10 | ... -> ... | 320k -> 182k | 23 -> 25 | 79.1 -> 78.2 |
| jackson3-ptv | mcp-toolsearch | 0/10 -> 10/10 | ... -> P.. | 9k -> 60k | 4 -> 8 | 8.5 -> 34.8 |
| jackson3-ptv | mcp-toolsearch-vector | 4/10 +2 inv (budget) -> 4/10 +3 inv | ... -> ... | 309k -> 53k | 18 -> 17 | 71.9 -> 42.9 |
| jackson3-ptv | shell | 6/10 (budget) -> 4/10 +1 inv (budget) | ... -> ... | 299k -> 305k | 19 -> 25 | 63.8 -> 227.5 |
| jackson3-ptv | web-brave | 3/10 +2 inv -> 5/10 +4 inv | ... -> ... | 281k -> 255k | 25 -> 25 | 97.0 -> 113.9 |
| jevjudge-gav | base | 2/15 -> 2/15 | ... -> ... | 2k -> 2k | 0 -> 0 | 27.1 -> 17.5 |
| jevjudge-gav | mcp | 15/15 -> 15/15 | PPP -> PPP | 40k -> 125k | 10 -> 23 | 16.8 -> 51.2 |
| jevjudge-gav | mcp-toolsearch | 1/15 -> 15/15 | ... -> PPP | 33k -> 37k | 10 -> 10 | 48.0 -> 23.2 |
| jevjudge-gav | mcp-toolsearch-vector | 15/15 -> 1/15 | PPP -> ... | 34k -> 35k | 5 -> 15 | 20.9 -> 35.8 |
| jevjudge-gav | shell | 1/15 -> 1/15 | ... -> ... | 27k -> 22k | 5 -> 4 | 12.9 -> 9.4 |
| jevjudge-gav | web-brave | 14/15 -> 14/15 | ... -> ... | 255k -> 273k | 25 -> 25 | 75.2 -> 70.3 |
| spring-ai-hostedtool | base | 1/7 -> 1/8 | ... -> ... | 1k -> 1k | 0 -> 0 | 18.3 -> 11.4 |
| spring-ai-hostedtool | mcp | 7/7 -> 3/8 | PP. -> ... | 17k -> 102k | 4 -> 25 | 11.2 -> 48.8 |
| spring-ai-hostedtool | mcp-toolsearch | 7/7 -> 2/8 (budget) | PP. -> ... | 19k -> 180k | 4 -> 23 | 14.1 -> 49.6 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 7/7 -> 2/8 +2 inv | PP. -> ... | 53k -> 216k | 8 -> 26 | 21.7 -> 70.7 |
| spring-ai-hostedtool | shell | 6/7 -> 2/8 (budget) | ... -> ... | 163k -> 292k | 24 -> 19 | 78.5 -> 57.1 |
| spring-ai-hostedtool | web-brave | 3/7 -> 7/8 | ... -> ... | 98k -> 106k | 11 -> 12 | 35.4 -> 26.7 |
