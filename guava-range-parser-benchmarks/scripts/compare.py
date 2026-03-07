#!/usr/bin/env python3
"""Compare two JMH benchmark result files and report differences."""

import json
import sys

if len(sys.argv) != 3:
    print(f"Usage: {sys.argv[0]} <baseline.json> <latest.json>")
    sys.exit(1)


def load_results(path):
    with open(path) as f:
        data = json.load(f)
    results = {}
    for r in data:
        # Use ClassName.methodName to avoid collisions across classes
        parts = r["benchmark"].rsplit(".", 2)
        key = f"{parts[-2]}.{parts[-1]}" if len(parts) >= 2 else r["benchmark"]
        if key in results:
            print(f"WARNING: duplicate benchmark key '{key}', keeping last entry")
        results[key] = r["primaryMetric"]["score"]
    return results


baseline = load_results(sys.argv[1])
latest = load_results(sys.argv[2])

print(f"{'Benchmark':<62}| {'Baseline':>10} | {'Latest':>10} | Change")
print(f"{'-' * 62}|{'-' * 12}|{'-' * 12}|--------")

for name in sorted(set(baseline) | set(latest)):
    bs = baseline.get(name)
    ls = latest.get(name)

    if bs is None:
        print(f"{name:<62}| {'N/A':>10} | {ls:>10.3f} | NEW")
    elif ls is None:
        print(f"{name:<62}| {bs:>10.3f} | {'N/A':>10} | REMOVED")
    else:
        pct = ((ls - bs) / bs * 100) if bs else 0
        flag = " !!!" if abs(pct) > 10 else ""
        print(f"{name:<62}| {bs:>10.3f} | {ls:>10.3f} | {pct:>+6.1f}%{flag}")