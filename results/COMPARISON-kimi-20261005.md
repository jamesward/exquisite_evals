# Kimi K2.5 runs: 2026-10-02 vs 2026-10-05

Same agent (`moonshotai.kimi-k2.5`), LLM judge (`deepseek.v3.2`) and javadocs.dev. What changed in between:

- Shell commands run in the Docker sandbox (before: on the host, with its environment)
- Jev's `grounded` pass bar 0.7 → 0.6
- Cut-off tool-call arguments get an error result and are repaired in the history (before: the run failed)
- References moved to typesafe-spring-ai 0.4.0 and spring-ai-agent-utils 0.13.0 (released in between; the checked APIs didn't change)

One trial per cell on each side, so single cells are noisy.

```
python3 compare.py results/20261002-125459-kimi-k2.5-judge-deepseek-v3.2 results/20261005-110328-kimi-k2.5-judge-deepseek-v3.2-sandbox
```

## By arm (before -> after)

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Tool calls | Avg time s |
|---|---|---|---|---|---|---|
| base | 0/4 -> 0/4 | 0/4 -> 0/4 | 0/4 -> 0/4 | 3k -> 4k | 0 -> 0 | 6 -> 11 |
| mcp | 3/4 -> 2/4 | 2/4 -> 2/4 | 2/4 -> 2/4 | 484k -> 380k | 51 -> 42 | 27 -> 26 |
| mcp-toolsearch | 2/4 -> 1/4 | 2/4 -> 1/4 | 2/4 -> 1/4 | 184k -> 463k | 39 -> 45 | 16 -> 38 |
| mcp-toolsearch-vector | 2/4 -> 2/4 | 2/4 -> 2/4 | 1/4 -> 2/4 | 440k -> 554k | 48 -> 49 | 45 -> 61 |
| shell | 0/4 -> 1/4 | 0/4 -> 1/4 | 0/4 -> 1/4 | 798k -> 679k | 83 -> 93 | 307 -> 213 |
| web-brave | 1/4 -> 0/4 | 1/4 -> 0/4 | 0/4 -> 0/4 | 842k -> 729k | 77 -> 75 | 68 -> 51 |

## By task x arm (verdicts = code checks / LLM / Jev, P = pass)

| Task | Arm | Facts | Verdicts | Total tokens | Tool calls | Time s |
|---|---|---|---|---|---|---|
| agentutils-shell-trap | base | 1/5 -> 0/5 | ... -> ... | 1k -> 1k | 0 -> 0 | 4.5 -> 6.0 |
| agentutils-shell-trap | mcp | 5/5 -> 5/5 | P.. -> PPP | 110k -> 32k | 16 -> 11 | 22.2 -> 14.0 |
| agentutils-shell-trap | mcp-toolsearch | 0/5 -> 0/5 | ... -> ... | 11k -> 6k | 14 -> 3 | 16.2 -> 9.9 |
| agentutils-shell-trap | mcp-toolsearch-vector | 5/5 -> 5/5 | PP. -> PPP | 82k -> 23k | 14 -> 8 | 27.8 -> 16.5 |
| agentutils-shell-trap | shell | 4/5 -> 0/5 | ... -> ... | 198k -> 115k | 25 -> 25 | 530.3 -> 224.7 |
| agentutils-shell-trap | web-brave | 4/5 -> 4/5 | ... -> ... | 244k -> 117k | 25 -> 15 | 70.4 -> 40.4 |
| jackson3-ptv | base | 3/10 -> 3/10 +1 inv | ... -> ... | 1k -> 2k | 0 -> 0 | 6.3 -> 19.3 |
| jackson3-ptv | mcp | 10/10 -> 9/10 | PPP -> ... | 25k -> 25k | 6 -> 6 | 13.8 -> 16.8 |
| jackson3-ptv | mcp-toolsearch | 7/10 -> 9/10 | ... -> ... | 71k -> 109k | 10 -> 16 | 18.3 -> 46.3 |
| jackson3-ptv | mcp-toolsearch-vector | 9/10 -> 9/10 | ... -> ... | 30k -> 28k | 7 -> 6 | 19.8 -> 30.8 |
| jackson3-ptv | shell | 5/10 (budget) -> 7/10 | ... -> ... | 282k -> 75k | 21 -> 18 | 139.3 -> 39.5 |
| jackson3-ptv | web-brave | 6/10 -> 7/10 | ... -> ... | 172k -> 253k | 16 -> 25 | 59.8 -> 68.8 |
| jevjudge-gav | base | 1/15 -> 0/15 | ... -> ... | 1k -> 1k | 0 -> 0 | 6.1 -> 5.8 |
| jevjudge-gav | mcp | 15/15 -> 15/15 | PPP -> PPP | 27k -> 27k | 8 -> 7 | 14.0 -> 19.9 |
| jevjudge-gav | mcp-toolsearch | 15/15 -> 15/15 | PPP -> PPP | 29k -> 37k | 7 -> 9 | 14.9 -> 24.4 |
| jevjudge-gav | mcp-toolsearch-vector | 15/15 -> 15/15 | PPP -> PPP | 35k -> 35k | 9 -> 10 | 26.3 -> 23.8 |
| jevjudge-gav | shell | 1/15 -> 0/15 +1 inv | ... -> ... | 257k -> 247k | 25 -> 25 | 533.7 -> 524.3 |
| jevjudge-gav | web-brave | 14/15 -> 14/15 | ... -> ... | 178k -> 94k | 16 -> 12 | 78.6 -> 31.7 |
| spring-ai-hostedtool | base | 2/8 -> 2/8 | ... -> ... | 1k -> 1k | 0 -> 0 | 6.8 -> 11.1 |
| spring-ai-hostedtool | mcp | 2/8 (budget) -> 2/8 (budget) | ... -> ... | 321k -> 296k | 21 -> 18 | 57.6 -> 53.5 |
| spring-ai-hostedtool | mcp-toolsearch | 8/8 -> 2/8 (budget) | PPP -> ... | 74k -> 311k | 8 -> 17 | 14.3 -> 72.9 |
| spring-ai-hostedtool | mcp-toolsearch-vector | 2/8 (budget) -> 1/8 (budget) | ... -> ... | 293k -> 467k | 18 -> 25 | 106.7 -> 174.6 |
| spring-ai-hostedtool | shell | 0/8 (error) -> 8/8 | ... -> PPP | 61k -> 242k | 12 -> 25 | 26.3 -> 61.8 |
| spring-ai-hostedtool | web-brave | 8/8 -> 6/8 | PP. -> ... | 248k -> 264k | 20 -> 23 | 61.2 -> 64.9 |
