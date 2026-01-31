package io.github.neewrobert.guavarangeparser.examples;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeFormatter;
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Examples demonstrating core parsing and formatting functionality.
 *
 * <p>The core module provides:
 *
 * <ul>
 *   <li>{@link RangeParser} - Parse string notation to Range objects
 *   <li>{@link RangeFormatter} - Format Range objects to string notation
 * </ul>
 */
@Component
@Order(1)
public class CoreExamples implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(CoreExamples.class);

  @Override
  public void run(String... args) {
    log.info("=== Core Module Examples ===");

    parsingIntegerRanges();
    parsingTemporalRanges();
    parsingDecimalRanges();
    formattingRanges();
    lenientParsing();
  }

  /** Demonstrates parsing various integer range notations. */
  private void parsingIntegerRanges() {
    log.info("\n--- Parsing Integer Ranges ---");

    RangeParser parser = RangeParser.builder().build();

    // Closed range: includes both endpoints [0, 100]
    Range<Integer> closed = parser.parseRange("[0..100]", Integer.class);
    log.info("[0..100]  -> {} (closed, contains 0 and 100)", closed);

    // Open range: excludes both endpoints (0, 100)
    Range<Integer> open = parser.parseRange("(0..100)", Integer.class);
    log.info("(0..100)  -> {} (open, excludes 0 and 100)", open);

    // Half-open range: includes lower, excludes upper [0, 100)
    Range<Integer> closedOpen = parser.parseRange("[0..100)", Integer.class);
    log.info("[0..100)  -> {} (closed-open, typical for array indices)", closedOpen);

    // Unbounded ranges with infinity
    Range<Integer> atLeast = parser.parseRange("[0..+∞)", Integer.class);
    log.info("[0..+∞)   -> {} (at least 0)", atLeast);

    Range<Integer> atMost = parser.parseRange("(-∞..100]", Integer.class);
    log.info("(-∞..100] -> {} (at most 100)", atMost);

    Range<Integer> all = parser.parseRange("(-∞..+∞)", Integer.class);
    log.info("(-∞..+∞)  -> {} (all integers)", all);
  }

  /** Demonstrates parsing temporal types like Duration and LocalDate. */
  private void parsingTemporalRanges() {
    log.info("\n--- Parsing Temporal Ranges ---");

    RangeParser parser = RangeParser.builder().build();

    // Duration ranges (ISO-8601 format)
    Range<Duration> durationRange = parser.parseRange("[PT1H..PT24H]", Duration.class);
    log.info("[PT1H..PT24H] -> {} (1 hour to 24 hours)", durationRange);

    // LocalDate ranges
    Range<LocalDate> dateRange = parser.parseRange("[2024-01-01..2024-12-31]", LocalDate.class);
    log.info("[2024-01-01..2024-12-31] -> {} (year 2024)", dateRange);

    // Instant ranges (ISO-8601 with timezone)
    Range<Instant> instantRange =
        parser.parseRange("[2024-01-01T00:00:00Z..2024-01-02T00:00:00Z]", Instant.class);
    log.info("[2024-01-01T00:00:00Z..2024-01-02T00:00:00Z] -> {}", instantRange);
  }

  /** Demonstrates parsing decimal and BigDecimal ranges. */
  private void parsingDecimalRanges() {
    log.info("\n--- Parsing Decimal Ranges ---");

    RangeParser parser = RangeParser.builder().build();

    // Double ranges
    Range<Double> doubleRange = parser.parseRange("[0.0..1.0]", Double.class);
    log.info("[0.0..1.0] -> {} (probability range)", doubleRange);

    // BigDecimal ranges (for financial calculations)
    Range<BigDecimal> priceRange = parser.parseRange("[9.99..99.99]", BigDecimal.class);
    log.info("[9.99..99.99] -> {} (price range)", priceRange);

    // Negative ranges
    Range<Double> temperatureRange = parser.parseRange("[-40.0..50.0]", Double.class);
    log.info("[-40.0..50.0] -> {} (temperature range in Celsius)", temperatureRange);
  }

  /** Demonstrates formatting Range objects back to string notation. */
  private void formattingRanges() {
    log.info("\n--- Formatting Ranges ---");

    RangeFormatter formatter = RangeFormatter.builder().build();

    // Format programmatically created ranges
    String closedStr = formatter.format(Range.closed(1, 10));
    log.info("Range.closed(1, 10) -> \"{}\"", closedStr);

    String openClosedStr = formatter.format(Range.openClosed(0, 100));
    log.info("Range.openClosed(0, 100) -> \"{}\"", openClosedStr);

    String atLeastStr = formatter.format(Range.atLeast(0));
    log.info("Range.atLeast(0) -> \"{}\"", atLeastStr);

    String allStr = formatter.format(Range.<Integer>all());
    log.info("Range.<Integer>all() -> \"{}\"", allStr);
  }

  /** Demonstrates lenient parsing mode for flexible input. */
  private void lenientParsing() {
    log.info("\n--- Lenient Parsing Mode ---");

    RangeParser lenientParser = RangeParser.builder().lenient(true).build();

    // Lenient mode accepts bracket-less notation
    Range<Integer> bracketless = lenientParser.parseRange("0..100", Integer.class);
    log.info("\"0..100\" (lenient) -> {} (treated as [0..100))", bracketless);

    // Alternative infinity notations
    Range<Integer> infNotation = lenientParser.parseRange("[0..INF)", Integer.class);
    log.info("\"[0..INF)\" (lenient) -> {} (INF accepted)", infNotation);
  }
}
