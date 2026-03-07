#!/usr/bin/env python3
"""Sanitize JMH results by removing machine-specific fields."""

import json
import sys

if len(sys.argv) != 3:
    print(f"Usage: {sys.argv[0]} <input.json> <output.json>")
    sys.exit(1)

data = json.load(open(sys.argv[1]))
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

json.dump(sanitized, open(sys.argv[2], "w"), indent=2)
print(f"Sanitized {len(sanitized)} benchmarks to {sys.argv[2]}")