# gpt-oss-120b vs Kimi K2.5 as the agent (same javadocs.dev, after the MCP changes)

- **Before (left):** agent `openai.gpt-oss-120b` (Responses API), LLM judge `openai.gpt-oss-120b`:
  `20261002-112143-post-mcp-changes` + `20261002-112526-post-mcp-changes-rerun` + `20261002-114020-post-mcp-changes-hostedtool-ref`
- **After (right):** agent `moonshotai.kimi-k2.5` (Chat Completions), LLM judge `deepseek.v3.2`:
  `20261002-125459-kimi-k2.5-judge-deepseek-v3.2`

Both sides ran against the same deployed javadocs.dev and the same task references, so the agent model is the main
variable. The LLM judge changed too (to a different model family than the agent), so LLM-judge pass rates are not
strictly comparable; the code checks and Jev are the same on both sides. One trial per cell.

```
python3 compare.py results/20261002-112143-post-mcp-changes,results/20261002-112526-post-mcp-changes-rerun,results/20261002-114020-post-mcp-changes-hostedtool-ref \
                   results/20261002-125459-kimi-k2.5-judge-deepseek-v3.2
```

## By arm (before -> after)

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Tool calls | Avg time s |
|---|---|---|---|---|---|---|
| base | 0/4 -> 0/4 | 0/4 -> 0/4 | 0/4 -> 0/4 | 10k -> 3k | 0 -> 0 | 16 -> 6 |
| mcp | 2/4 -> 3/4 | 2/4 -> 2/4 | 2/4 -> 2/4 | 688k -> 484k | 97 -> 51 | 62 -> 27 |
| mcp-toolsearch | 3/4 -> 2/4 | 2/4 -> 2/4 | 1/4 -> 2/4 | 385k -> 184k | 57 -> 39 | 42 -> 16 |
| mcp-toolsearch-vector | 0/4 -> 2/4 | 0/4 -> 2/4 | 0/4 -> 1/4 | 491k -> 440k | 77 -> 48 | 50 -> 45 |
| shell | 0/4 -> 0/4 | 0/4 -> 0/4 | 0/4 -> 0/4 | 905k -> 798k | 73 -> 83 | 126 -> 307 |
| web-brave | 0/4 -> 1/4 | 0/4 -> 1/4 | 0/4 -> 0/4 | 882k -> 842k | 87 -> 77 | 76 -> 68 |

## By task x arm (verdicts = code checks / LLM / Jev, P = pass)

| Task | Arm | Facts | Verdicts | Total tokens | Tool calls | Time s |
|---|---|---|---|---|---|---|
| agentutils-shell-trap | base | 1/5 +2 inv -> 1/5 | ... -> ... | 3k -> 1k | 0 -> 0 | 15.1 -> 4.5 |
| agentutils-shell-trap | mcp | 5/5 (budget) -> 5/5 | PPP -> P.. | 279k -> 110k | 24 -> 16 | 71.0 -> 22.2 |
| agentutils-shell-trap | mcp-toolsearch | 5/5 -> 0/5 | PP. -> ... | 108k -> 11k | 16 -> 14 | 59.2 -> 16.2 |
| agentutils-shell-trap | mcp-toolsearch-vector | 0/5 -> 5/5 | ... -> PP. | 187k -> 82k | 19 -> 14 | 52.2 -> 27.8 |
| agentutils-shell-trap | shell | 1/5 +1 inv (budget) -> 4/5 | ... -> ... | 286k -> 198k | 25 -> 25 | 209.1 -> 530.3 |
| agentutils-shell-trap | web-brave | 3/5 +1 inv -> 4/5 | ... -> ... | 248k -> 244k | 25 -> 25 | 91.7 -> 70.4 |
| jackson3-ptv | base | 3/10 +11 inv -> 3/10 | ... -> ... | 4k -> 1k | 0 -> 0 | 21.8 -> 6.3 |
| jackson3-ptv | mcp | 6/10 -> 10/10 | ... -> PPP | 182k -> 25k | 25 -> 6 | 78.2 -> 13.8 |
| jackson3-ptv | mcp-toolsearch | 10/10 -> 7/10 | P.. -> ... | 60k -> 71k | 8 -> 10 | 34.8 -> 18.3 |
| jackson3-ptv | mcp-toolsearch-vector | 4/10 +3 inv -> 9/10 | ... -> ... | 53k -> 30k | 17 -> 7 | 42.9 -> 19.8 |
| jackson3-ptv | shell | 4/10 +1 inv (budget) -> 5/10 (budget) | ... -> ... | 305k -> 282k | 25 -> 21 | 227.5 -> 139.3 |
| jackson3-ptv | web-brave | 5/10 +4 inv -> 6/10 | ... -> ... | 255k -> 172k | 25 -> 16 | 113.9 -> 59.8 |
| jevjudge-gav | base | 2/15 -> 1/15 | ... -> ... | 2k -> 1k | 0 -> 0 | 17.5 -> 6.1 |
| jevjudge-gav | mcp | 15/15 -> 15/15 | PPP -> PPP | 125k -> 27k | 23 -> 8 | 51.2 -> 14.0 |
| jevjudge-gav | mcp-toolsearch | 15/15 -> 15/15 | PPP -> PPP | 37k -> 29k | 10 -> 7 | 23.2 -> 14.9 |
| jevjudge-gav | mcp-toolsearch-vector | 1/15 -> 15/15 | ... -> PPP | 35k -> 35k | 15 -> 9 | 35.8 -> 26.3 |
| jevjudge-gav | shell | 1/15 -> 1/15 | ... -> ... | 22k -> 257k | 4 -> 25 | 9.4 -> 533.7 |
| jevjudge-gav | web-brave | 14/15 -> 14/15 | ... -> ... | 273k -> 178k | 25 -> 16 | 70.3 -> 78.6 |
| spring-ai-hostedtool | base | 1/8 -> 2/8 | ... -> ... | 1k -> 1k | 0 -> 0 | 11.4 -> 6.8 |
| spring-ai-hostedtool | mcp | 3/8 -> 2/8 (budget) | ... -> ... | 102k -> 321k | 25 -> 21 | 48.8 -> 57.6 |
| spring-ai-hostedtool | mcp-toolsearch | 2/8 (budget) -> 8/8 | ... -> PPP | 180k -> 74k | 23 -> 8 | 49.6 -> 14.3 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 2/8 +2 inv -> 2/8 (budget) | ... -> ... | 216k -> 293k | 26 -> 18 | 70.7 -> 106.7 |
| spring-ai-hostedtool | shell | 2/8 (budget) -> 0/8 (error) | ... -> ... | 292k -> 61k | 19 -> 12 | 57.1 -> 26.3 |
| spring-ai-hostedtool | web-brave | 7/8 -> 8/8 | ... -> PP. | 106k -> 248k | 12 -> 20 | 26.7 -> 61.2 |
