#!/usr/bin/env python3
"""Draws 3d-evals.svg: the eval results as a Task x Arm x Judge cube, one layer per judge.

Each cell is one judge's score for one task x arm answer (red 0 -> green 1, check mark = pass).
Regenerate from another run with: python3 make_3d_evals.py ../results/<run-dir>
"""
import json
import sys
from pathlib import Path

RUN = Path(sys.argv[1] if len(sys.argv) > 1 else
           Path(__file__).parent.parent / "results/20261006-075758-pre-mcp-changes-clean")
ARMS = ["base", "shell", "web-brave", "mcp"]
TASKS = ["jevjudge-gav", "jackson3-ptv", "spring-ai-hostedtool", "agentutils-shell-trap"]
LAYERS = [("checks", "Code checks", "facts found"), ("llm", "LLM judge", "DeepSeek V3.2"), ("jev", "Jev", "TypeSafe")]
HIGHLIGHT = ("mcp", "jevjudge-gav")  # one answer, followed through all three judges

results = {(r["run"]["arm"], r["run"]["task"]): r for r in json.loads((RUN / "results.json").read_text())}


def verdict(layer, arm, task):
    r = results[(arm, task)]
    if layer == "checks":
        c = r["checks"]
        found, missing = len(c.get("found", [])), len(c.get("missing", []))
        return found / max(1, found + missing), c["passed"]
    j = next(j for j in r["judges"] if j["judge"] == layer)
    return j["score"], j["passed"]


# Isometric-ish projection: u runs along arms, v along tasks, h is the layer height.
A, B, CELL, GAP = 0.866, 0.45, 56, 205
X0, Y0 = 330, 70


def pt(u, v, layer):
    return X0 + (u - v) * CELL * A, Y0 + (u + v) * CELL * B + layer * GAP


def poly(points, **attrs):
    d = " ".join(f"{x:.1f},{y:.1f}" for x, y in points)
    extra = " ".join(f'{k.replace("_", "-")}="{v}"' for k, v in attrs.items())
    return f'<polygon points="{d}" {extra}/>'


def colour(score):
    hue = 120 * max(0.0, min(1.0, score))  # red -> amber -> green
    return f"hsl({hue:.0f},65%,{58 - 8 * score:.0f}%)"


def text(x, y, s, size=15, anchor="start", rotate=None, weight="normal", fill="#333"):
    t = f' transform="rotate({rotate:.1f} {x:.1f} {y:.1f})"' if rotate is not None else ""
    return (f'<text x="{x:.1f}" y="{y:.1f}" font-size="{size}" text-anchor="{anchor}" font-weight="{weight}" '
            f'fill="{fill}"{t}>{s}</text>')


out = []
n = len(ARMS)
angle = 27.5  # atan(B / A) in degrees: the direction of the plane's edges
for li, (key, name, sub) in enumerate(LAYERS):
    out.append(poly([pt(0, 0, li), pt(n, 0, li), pt(n, n, li), pt(0, n, li)],
                    fill="#f4f6f8", stroke="#9aa5b1", stroke_width=1.5, opacity="0.97"))
    for i, arm in enumerate(ARMS):
        for j, task in enumerate(TASKS):
            score, passed = verdict(key, arm, task)
            g = 0.06
            corners = [pt(i + g, j + g, li), pt(i + 1 - g, j + g, li), pt(i + 1 - g, j + 1 - g, li), pt(i + g, j + 1 - g, li)]
            hi = (arm, task) == HIGHLIGHT
            out.append(poly(corners, fill=colour(score), stroke="#1f2933" if hi else "white",
                            stroke_width=3 if hi else 1))
            if passed:
                cx, cy = pt(i + 0.5, j + 0.5, li)
                out.append(text(cx, cy + 6, "✓", size=19, anchor="middle", weight="bold", fill="white"))
    rx, ry = pt(n, 0, li)
    out.append(text(rx + 18, ry + 2, name, size=21, weight="bold"))
    out.append(text(rx + 18, ry + 24, sub, size=15, fill="#667"))

# The highlighted answer, dropped through every layer: dashed segments in the gaps between layers, from the
# cell's bottom corner to the same cell's top corner on the next layer (both on the same vertical line).
hi, hj = ARMS.index(HIGHLIGHT[0]), TASKS.index(HIGHLIGHT[1])
for li in range(len(LAYERS) - 1):
    (x1, y1), (_, y2) = pt(hi + 1, hj + 1, li), pt(hi, hj, li + 1)
    out.append(f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x1:.1f}" y2="{y2:.1f}" stroke="#1f2933" '
               f'stroke-width="2" stroke-dasharray="5,5"/>')

# Axis labels on the bottom layer's two front edges.
bottom = len(LAYERS) - 1
for i, arm in enumerate(ARMS):
    x, y = pt(i + 0.5, n, bottom)
    out.append(text(x - 6, y + 12, arm, anchor="end", rotate=-angle, size=16))
for j, task in enumerate(TASKS):
    x, y = pt(n, j + 0.5, bottom)
    out.append(text(x + 6, y + 12, task, rotate=angle, size=16))
lx, ly = pt(2, n + 2.2, bottom)
out.append(text(lx, ly, "Arm", size=20, anchor="middle", weight="bold", rotate=angle, fill="#1f6feb"))
tx, ty = pt(n + 3.9, 2, bottom)
out.append(text(tx, ty, "Task", size=20, anchor="middle", weight="bold", rotate=-angle, fill="#1f6feb"))

# Judge axis: up the left side.
jx = pt(0, n, 0)[0] - 30
jy0, jy1 = pt(0, n, bottom)[1] - 30, pt(0, n, 0)[1] - 50
out.append(f'<line x1="{jx:.1f}" y1="{jy0:.1f}" x2="{jx:.1f}" y2="{jy1:.1f}" stroke="#1f6feb" stroke-width="3" '
           f'marker-end="url(#arrow)"/>')
out.append(text(jx - 10, (jy0 + jy1) / 2, "Judge", size=20, anchor="middle", weight="bold", rotate=-90, fill="#1f6feb"))

# Legend.
LX, LY = 120, 775
out.append(text(LX, LY, "score", size=14, anchor="end", fill="#667"))
for k in range(11):
    out.append(f'<rect x="{LX + 8 + k * 14}" y="{LY - 12}" width="14" height="14" fill="{colour(k / 10)}"/>')
out.append(text(LX + 8, LY + 18, "0", size=13, fill="#667"))
out.append(text(LX + 8 + 11 * 14, LY + 18, "1", size=13, anchor="end", fill="#667"))
out.append(text(LX + 180, LY, "✓ pass", size=14, fill="#667"))
out.append(text(LX + 180, LY + 18, "--- one answer, three verdicts", size=14, fill="#667"))
agent = json.loads((RUN / "meta.json").read_text()).get("agent", "?")
out.append(text(LX - 50, LY + 44, f"each cell: one judge's score for one answer · agent {agent}, run {RUN.name[:8]}",
                size=14, fill="#667"))

W, H = 820, 830
svg = f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {W} {H}" width="{W}" height="{H}" font-family="Helvetica, Arial, sans-serif">
<defs><marker id="arrow" viewBox="0 0 10 10" refX="5" refY="5" markerWidth="5" markerHeight="5" orient="auto-start-reverse">
<path d="M 0 0 L 10 5 L 0 10 z" fill="#1f6feb"/></marker></defs>
{chr(10).join(out)}
</svg>
'''
(Path(__file__).parent / "3d-evals.svg").write_text(svg)
print("wrote 3d-evals.svg from", RUN)
