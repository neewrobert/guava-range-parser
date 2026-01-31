# Guava Range Parser

A Java library for parsing and formatting Guava Range objects from string notation (e.g., `[0..100)`).

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://openjdk.org/)

## Overview

Guava's `Range` class is powerful but lacks a built-in way to parse string notation back into Range objects. This library fills that gap by providing:

- **String notation parsing**: Parse `[0..100)`, `(-∞..+∞)`, etc. into Range objects
- **Formatting**: Convert Range objects back to string notation
- **Jackson integration**: Serialize/deserialize Range as string notation in JSON
- **Spring Boot integration**: Auto-configured converters for configuration properties

## Installation

### Maven

```xml
<!-- Core module (required) -->
<dependency>
    <groupId>io.github.neewrobert</groupId>
    <artifactId>guava-range-parser-core</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>

<!-- Jackson module (optional) -->
<dependency>
    <groupId>io.github.neewrobert</groupId>
    <artifactId>guava-range-parser-jackson</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>

<!-- Spring Boot module (optional) -->
<dependency>
    <groupId>io.github.neewrobert</groupId>
    <artifactId>guava-range-parser-spring</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Using BOM (recommended)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.neewrobert</groupId>
            <artifactId>guava-range-parser-bom</artifactId>
            <version>0.1.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Quick Start

### Basic Parsing

```java
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import com.google.common.collect.Range;

// Parse integer ranges
Range<Integer> range = RangeParser.parse("[0..100)", Integer.class);
// Returns: Range.closedOpen(0, 100)

// Parse with infinity
Range<Integer> unbounded = RangeParser.parse("[100..+∞)", Integer.class);
// Returns: Range.atLeast(100)

// Parse duration ranges
Range<Duration> duration = RangeParser.parse("[PT1H..PT24H)", Duration.class);
// Returns: Range.closedOpen(Duration.ofHours(1), Duration.ofHours(24))
```

### Formatting

```java
import io.github.neewrobert.guavarangeparser.core.RangeFormatter;

String notation = RangeFormatter.toString(Range.closedOpen(0, 100));
// Returns: "[0..100)"

String unbounded = RangeFormatter.toString(Range.atLeast(100));
// Returns: "[100..+∞)"
```

### Jackson Integration

Serialize and deserialize Range objects as **string notation** in JSON:

```java
import io.github.neewrobert.guavarangeparser.jackson.GuavaRangeParserModule;

ObjectMapper mapper = new ObjectMapper()
    .registerModule(new GuavaRangeParserModule());

// Deserialize from string notation
Range<Integer> range = mapper.readValue("\"[0..100)\"",
    new TypeReference<Range<Integer>>() {});

// Serialize to string notation
String json = mapper.writeValueAsString(Range.closedOpen(0, 100));
// Returns: "[0..100)"
```

> **Note:** This module uses compact string notation (`"[0..100)"`). For JSON object format
> (`{"lowerEndpoint":0,"upperEndpoint":100,...}`), use Jackson's
> [jackson-datatype-guava](https://github.com/FasterXML/jackson-datatypes-collections) module instead.

### Spring Boot Integration

Simply add the dependency and use Range types in your configuration:

```java
@ConfigurationProperties(prefix = "my-app")
public class MyProperties {
    private Range<Integer> stockRange;
    private Range<Duration> refreshInterval;

    // getters and setters
}
```

```properties
my-app.stock-range=[0..100)
my-app.refresh-interval=[PT1M..PT5M]
```

## Supported Notation

| Notation | Range Type | Description |
|----------|-----------|-------------|
| `[a..b]` | `closed(a, b)` | Both endpoints inclusive |
| `(a..b)` | `open(a, b)` | Both endpoints exclusive |
| `[a..b)` | `closedOpen(a, b)` | Lower inclusive, upper exclusive |
| `(a..b]` | `openClosed(a, b)` | Lower exclusive, upper inclusive |
| `[a..+∞)` | `atLeast(a)` | Lower bounded only |
| `(a..+∞)` | `greaterThan(a)` | Greater than a |
| `(-∞..b]` | `atMost(b)` | Upper bounded only |
| `(-∞..b)` | `lessThan(b)` | Less than b |
| `(-∞..+∞)` | `all()` | Unbounded |

## Supported Types

### Built-in Type Adapters

- **Numeric**: `Integer`, `Long`, `Short`, `Byte`, `Double`, `Float`, `BigInteger`, `BigDecimal`
- **Temporal**: `Duration`, `Instant`, `LocalDate`, `LocalDateTime`, `LocalTime`, `ZonedDateTime`, `OffsetDateTime`
- **Other**: `String`, `Character`

### Custom Types

Register custom type adapters for your own Comparable types:

```java
RangeParser parser = RangeParser.builder()
    .registerType(Money.class, Money::parse)
    .build();

Range<Money> range = parser.parseRange("[$10..$100)", Money.class);
```

## Configuration Options

### Infinity Style

Choose how infinity is represented in output:

```java
RangeFormatter formatter = RangeFormatter.builder()
    .infinityStyle(InfinityStyle.SYMBOL)     // +∞, -∞ (default)
    .infinityStyle(InfinityStyle.WORD_LOWER) // +inf, -inf
    .infinityStyle(InfinityStyle.WORD_UPPER) // +INF, -INF
    .infinityStyle(InfinityStyle.WORD_FULL)  // +Infinity, -Infinity
    .build();
```

The Jackson module also supports configuring infinity style:

```java
GuavaRangeParserModule module = GuavaRangeParserModule.builder()
    .infinityStyle(InfinityStyle.WORD_LOWER)  // Use +inf/-inf in JSON
    .build();
```

### Lenient Parsing

Enable lenient mode to accept variations:

```java
RangeParser parser = RangeParser.builder()
    .lenient(true)
    .build();

// All of these work in lenient mode:
parser.parseRange("[0..100)", Integer.class);  // Standard
parser.parseRange("0..100", Integer.class);    // No brackets (assumes closedOpen)
```

## Building

```bash
mvn clean install
```

## Requirements

- Java 17 or higher
- Guava 31 or higher

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

- [Google Guava](https://github.com/google/guava) for the excellent Range API
- Inspired by [Guava Issue #1911](https://github.com/google/guava/issues/1911) and [#2090](https://github.com/google/guava/issues/2090)