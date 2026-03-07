#!/usr/bin/env python3
"""Compare two JMH benchmark result files and report differences."""

import json
import sys

if len(sys.argv) != 3:
    print(f"Usage: {sys.argv[0]} <baseline.json> <latest.json>")
    sys.exit(1)

baseline = {
    r["benchmark"].split(".")[-1]: r["primaryMetric"]["score"]
    for r in json.load(open(sys.argv[1]))
}
latest = {
    r["benchmark"].split(".")[-1]: r["primaryMetric"]["score"]
    for r in json.load(open(sys.argv[2]))
}

print(f"Benchmark{'':42s}| Baseline   | Latest     | Change")
print(f"{'-' * 51}|{'-' * 12}|{'-' * 12}|--------")

for name in sorted(set(baseline) | set(latest)):
    bs = baseline.get(name, 0)
    ls = latest.get(name, 0)
    pct = ((ls - bs) / bs * 100) if bs else 0
    flag = " !!!" if abs(pct) > 10 else ""
    print(f"{name:<51}| {bs:>9.3f}  | {ls:>9.3f}  | {pct:>+6.1f}%{flag}")