#!/usr/bin/env python3
"""Compare eval runs: python3 compare.py BEFORE_DIR[,BEFORE_DIR...] AFTER_DIR[,AFTER_DIR...]

Each side is one or more result directories (comma-separated) containing a summary.csv; rows from the
directories on one side are merged (e.g. the main baseline run plus the separate vector-arm run).
Prints per-arm totals for both sides and a per task x arm table of what changed. No dependencies.
"""
import csv
import sys
from pathlib import Path


def load(side: str) -> dict:
    rows = {}
    for d in side.split(","):
        with open(Path(d) / "summary.csv", newline="") as f:
            for r in csv.DictReader(f):
                rows[(r["task"], r["arm"])] = r
    return rows


def truthy(v: str) -> bool:
    return str(v).strip().lower() == "true"


def num(v: str) -> float:
    try:
        return float(v)
    except (TypeError, ValueError):
        return 0.0


def total_tokens(r: dict) -> float:
    return num(r["in_tokens"]) + num(r["out_tokens"]) + num(r["in_tool_tokens"])


def verdicts(r: dict) -> str:
    return "".join("P" if truthy(r[k]) else "." for k in ("checks_pass", "llm_pass", "jev_pass"))


def per_arm(rows: dict) -> dict:
    arms = {}
    for (_, arm), r in rows.items():
        a = arms.setdefault(arm, {"n": 0, "checks": 0, "llm": 0, "jev": 0, "tokens": 0.0, "time": 0.0, "calls": 0.0})
        a["n"] += 1
        a["checks"] += truthy(r["checks_pass"])
        a["llm"] += truthy(r["llm_pass"])
        a["jev"] += truthy(r["jev_pass"])
        a["tokens"] += total_tokens(r)
        a["time"] += num(r["time_s"])
        a["calls"] += num(r["tool_calls"])
    return arms


def main() -> None:
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    before, after = load(sys.argv[1]), load(sys.argv[2])
    b_arms, a_arms = per_arm(before), per_arm(after)

    print("## By arm (before -> after)\n")
    print("| Arm | Code checks pass | LLM judge pass | Jev pass | Total tokens | Tool calls | Avg time s |")
    print("|---|---|---|---|---|---|---|")
    for arm in sorted(set(b_arms) | set(a_arms)):
        b, a = b_arms.get(arm), a_arms.get(arm)

        def cell(key, fmt=lambda s, n: f"{s}/{n}"):
            bs = fmt(b[key], b["n"]) if b else "-"
            as_ = fmt(a[key], a["n"]) if a else "-"
            return f"{bs} -> {as_}"

        k = lambda s, n: f"{s / 1000:.0f}k"
        avg = lambda s, n: f"{s / n:.0f}"
        print(f"| {arm} | {cell('checks')} | {cell('llm')} | {cell('jev')} | {cell('tokens', k)} | "
              f"{cell('calls', lambda s, n: f'{s:.0f}')} | {cell('time', avg)} |")

    print("\n## By task x arm (verdicts = code checks / LLM / Jev, P = pass)\n")
    print("| Task | Arm | Facts | Verdicts | Total tokens | Tool calls | Time s |")
    print("|---|---|---|---|---|---|---|")
    for key in sorted(set(before) | set(after)):
        b, a = before.get(key), after.get(key)
        if not (b and a):
            continue

        def facts(r):
            extra = (f" +{r['invented']} inv" if num(r["invented"]) else "") + (" (budget)" if truthy(r["budget"]) else "") \
                + (" (error)" if truthy(r["run_error"]) else "")
            return f"{r['facts_found']}/{r['facts_total']}{extra}"

        print(f"| {key[0]} | {key[1]} | {facts(b)} -> {facts(a)} | {verdicts(b)} -> {verdicts(a)} | "
              f"{total_tokens(b) / 1000:.0f}k -> {total_tokens(a) / 1000:.0f}k | {b['tool_calls']} -> {a['tool_calls']} | "
              f"{b['time_s']} -> {a['time_s']} |")


if __name__ == "__main__":
    main()
