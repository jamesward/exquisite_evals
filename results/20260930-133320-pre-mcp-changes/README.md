# Baseline run, 2026-09-30 13:33 (before the javadocs.dev MCP changes)

All 4 tasks x 5 arms (base, shell, web-brave, mcp, mcp-toolsearch), one trial each.

The raw `report.md` / `results.json` of this run were lost: they were written under `build/evals/`,
which a later `./gradlew clean` deleted. `summary.csv` here is **reconstructed from the per-run table
in the project README**, so it has the same metrics (facts, verdicts, scores, tokens, calls, time) but
no answers, tool-call traces or per-criterion judge details.
