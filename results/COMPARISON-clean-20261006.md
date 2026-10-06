# javadocs.dev before vs after the MCP changes (clean runs, 2026-10-06)

Same four tasks and arms, agent `moonshotai.kimi-k2.5`, LLM judge `deepseek.v3.2`, Jev, Docker sandbox, and run budgets. Both runs used `-Pinspector`. One trial per cell: treat arm-level changes outside `mcp` as run-to-run variance, since those arms do not use javadocs.dev.

Before: `results/20261006-075758-pre-mcp-changes-clean`  
After: `results/20261006-085428-post-mcp-changes-clean`

## By arm (before -> after)

| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Tool calls | Avg time s |
|---|---|---|---|---|---|---|
| base | 0/4 -> 0/4 | 0/4 -> 0/4 | 0/4 -> 0/4 | 5k -> 3k | 0 -> 0 | 13 -> 7 |
| mcp | 3/4 -> 2/4 | 3/4 -> 2/4 | 3/4 -> 1/4 | 254k -> 564k | 31 -> 50 | 18 -> 44 |
| shell | 1/4 -> 0/4 | 1/4 -> 0/4 | 1/4 -> 0/4 | 813k -> 744k | 100 -> 93 | 318 -> 261 |
| web-brave | 0/4 -> 0/4 | 0/4 -> 0/4 | 0/4 -> 0/4 | 735k -> 753k | 73 -> 69 | 71 -> 58 |

## By task x arm (verdicts = code checks / LLM / Jev, P = pass)

| Task | Arm | Facts | Verdicts | Total tokens | Tool calls | Time s |
|---|---|---|---|---|---|---|
| agentutils-shell-trap | base | 0/5 -> 1/5 | ... -> ... | 1k -> 1k | 0 -> 0 | 9.4 -> 6.9 |
| agentutils-shell-trap | mcp | 5/5 -> 5/5 | PPP -> PP. | 47k -> 21k | 10 -> 12 | 16.4 -> 15.0 |
| agentutils-shell-trap | shell | 1/5 -> 0/5 | ... -> ... | 136k -> 148k | 25 -> 19 | 251.1 -> 372.7 |
| agentutils-shell-trap | web-brave | 1/5 -> 4/5 | ... -> ... | 215k -> 239k | 25 -> 22 | 78.1 -> 69.2 |
| jackson3-ptv | base | 3/10 +1 inv -> 4/10 | ... -> ... | 1k -> 1k | 0 -> 0 | 21.0 -> 10.4 |
| jackson3-ptv | mcp | 10/10 -> 8/10 | PPP -> ... | 144k -> 109k | 7 -> 11 | 27.3 -> 26.7 |
| jackson3-ptv | shell | 6/10 -> 4/10 | ... -> ... | 232k -> 170k | 25 -> 25 | 201.0 -> 82.9 |
| jackson3-ptv | web-brave | 9/10 -> 7/10 | ... -> ... | 265k -> 167k | 25 -> 19 | 87.3 -> 60.4 |
| jevjudge-gav | base | 1/15 -> 0/15 | ... -> ... | 1k -> 0k | 0 -> 0 | 7.9 -> 4.2 |
| jevjudge-gav | mcp | 15/15 -> 15/15 | PPP -> PPP | 40k -> 26k | 9 -> 6 | 19.1 -> 14.2 |
| jevjudge-gav | shell | 0/15 +1 inv -> 1/15 (budget) | ... -> ... | 244k -> 275k | 25 -> 25 | 750.4 -> 491.6 |
| jevjudge-gav | web-brave | 14/15 -> 14/15 | ... -> ... | 142k -> 109k | 13 -> 9 | 46.0 -> 35.3 |
| spring-ai-hostedtool | base | 2/8 -> 2/8 | ... -> ... | 1k -> 1k | 0 -> 0 | 12.5 -> 5.3 |
| spring-ai-hostedtool | mcp | 7/8 -> 2/8 (budget) | ... -> ... | 24k -> 407k | 5 -> 21 | 11.2 -> 122.0 |
| spring-ai-hostedtool | shell | 8/8 -> 6/8 | PPP -> ... | 201k -> 151k | 25 -> 24 | 68.1 -> 98.3 |
| spring-ai-hostedtool | web-brave | 6/8 -> 6/8 | ... -> ... | 114k -> 238k | 10 -> 19 | 73.4 -> 67.7 |
