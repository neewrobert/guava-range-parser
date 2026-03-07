# Contributing to Guava Range Parser

Thanks for your interest in contributing! This document provides guidelines to make the process smooth for everyone.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/guava-range-parser.git`
3. Create a branch: `git checkout -b my-feature`

## Development Setup

**Requirements:**
- Java 17+
- Maven 3.8+
- Python 3 (only for benchmark comparison scripts)

**Common commands** (run `make help` for the full list):
```bash
make build          # Compile
make test           # Run tests
make format         # Format code with google-java-format
make coverage       # Run tests with JaCoCo coverage report
make benchmark-quick # Quick performance check (~2 min)
```

## Making Changes

### Code Style

We use [Google Java Format](https://github.com/google/google-java-format). Format your code before committing:

```bash
mvn fmt:format
```

The CI will reject PRs with formatting issues.

### Testing

- Add tests for new features
- Ensure all tests pass: `mvn test`
- We use mutation testing to ensure test quality: `mvn test pitest:mutationCoverage -pl <module> -Ppitest`

### Commit Messages

Write clear commit messages that explain *why* the change was made:

```
Add support for BigInteger ranges

BigInteger is commonly used for arbitrary-precision integers.
This adds a built-in TypeAdapter for parsing BigInteger values.
```

### Benchmarks

We use [JMH](https://github.com/openjdk/jmh) to track performance. You should run benchmarks when your changes touch parsing or formatting logic (e.g., `RangeParser`, `RangeFormatter`, `TypeAdapter`, `BuiltInTypeAdapters`).

**When to run:**
- Before and after changes to parsing/formatting code
- When adding new type adapters (to verify they don't regress existing performance)
- Not needed for documentation, test-only, or dependency-update changes

**Quick check (~2 min):**
```bash
make benchmark-quick
```

**Full run (~10 min, more reliable):**
```bash
make benchmark
```

**Compare against baseline:**
```bash
make benchmark-compare
```

This shows a table comparing your results against the last saved baseline. Changes over ±10% are flagged. If you see a regression, investigate before submitting your PR.

> Note: `benchmark-save` and `benchmark-compare` require `python3`.

You don't need to update the baseline file — maintainers will do that at release time.

## Pull Request Process

1. Update tests for your changes
2. Run `mvn clean verify` locally
3. Run `mvn fmt:format` to fix formatting
4. Push to your fork and open a PR

That's it! We'll review your PR and provide feedback.

## Reporting Issues

Found a bug or have a feature request? [Open an issue](https://github.com/neewrobert/guava-range-parser/issues/new).

Include:
- What you expected to happen
- What actually happened
- Steps to reproduce (if applicable)
- Java version and environment details

## Questions?

Feel free to open an issue for questions. There are no dumb questions!

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).