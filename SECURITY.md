# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.1.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in this project, please report it responsibly.

**Do not open a public issue for security vulnerabilities.**

Instead, please send an email to the maintainers or use [GitHub's private vulnerability reporting](https://github.com/neewrobert/guava-range-parser/security/advisories/new).

### What to Include

- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

### Response Timeline

- **Initial response:** Within 48 hours
- **Status update:** Within 7 days
- **Fix timeline:** Depends on severity, typically within 30 days

## Security Measures

This library implements several security measures:

- **Input length validation:** Maximum input length of 1000 characters to prevent resource exhaustion
- **No regex in parsing:** Manual parsing to prevent ReDoS attacks
- **Runtime validation:** Bounds checking for infinity values and range validity

## Scope

This security policy applies to the latest released version of guava-range-parser and its modules:

- `guava-range-parser-core`
- `guava-range-parser-jackson`
- `guava-range-parser-spring`
