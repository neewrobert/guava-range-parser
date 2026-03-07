#!/usr/bin/env python3
"""Sanitize JMH results by removing machine-specific fields."""

import json
import sys

if len(sys.argv) != 3:
    print(f"Usage: {sys.argv[0]} <input.json> <output.json>")
    sys.exit(1)

with open(sys.argv[1]) as f:
    data = json.load(f)

sanitized = []
for r in data:
    sanitized.append({
        "benchmark": r["benchmark"],
        "mode": r["mode"],
        "threads": r["threads"],
        "forks": r["forks"],
        "measurementIterations": r["measurementIterations"],
        "measurementTime": r["measurementTime"],
        "warmupIterations": r["warmupIterations"],
        "warmupTime": r["warmupTime"],
        "primaryMetric": {
            "score": r["primaryMetric"]["score"],
            "scoreError": r["primaryMetric"]["scoreError"],
            "scoreUnit": r["primaryMetric"]["scoreUnit"],
        },
    })

with open(sys.argv[2], "w") as f:
    json.dump(sanitized, f, indent=2)

print(f"Sanitized {len(sanitized)} benchmarks to {sys.argv[2]}")