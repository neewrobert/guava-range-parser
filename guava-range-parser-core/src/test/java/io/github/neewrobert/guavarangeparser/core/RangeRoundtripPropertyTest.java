package io.github.neewrobert.guavarangeparser.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Range;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;

/**
 * Property-based tests verifying the roundtrip property: {@code parse(format(range)) == range}.
 *
 * <p>These tests use jqwik to generate thousands of random Range instances and verify that
 * formatting and parsing them back produces the same range.
 */
class RangeRoundtripPropertyTest {

  private final RangeParser parser = RangeParser.builder().build();
  private final RangeFormatter formatter = RangeFormatter.builder().build();

  // ==========================================================================
  // Integer Ranges
  // ==========================================================================

  @Property
  void integerClosedRangeRoundtrip(
      @ForAll @IntRange(min = -10000, max = 10000) int lower,
      @ForAll @IntRange(min = -10000, max = 10000) int upper) {
    if (lower <= upper) {
      Range<Integer> original = Range.closed(lower, upper);
      assertRoundtrip(original, Integer.class);
    }
  }

  @Property
  void integerOpenRangeRoundtrip(
      @ForAll @IntRange(min = -10000, max = 10000) int lower,
      @ForAll @IntRange(min = -10000, max = 10000) int upper) {
    if (lower < upper) {
      Range<Integer> original = Range.open(lower, upper);
      assertRoundtrip(original, Integer.class);
    }
  }

  @Property
  void integerClosedOpenRangeRoundtrip(
      @ForAll @IntRange(min = -10000, max = 10000) int lower,
      @ForAll @IntRange(min = -10000, max = 10000) int upper) {
    if (lower < upper) {
      Range<Integer> original = Range.closedOpen(lower, upper);
      assertRoundtrip(original, Integer.class);
    }
  }

  @Property
  void integerOpenClosedRangeRoundtrip(
      @ForAll @IntRange(min = -10000, max = 10000) int lower,
      @ForAll @IntRange(min = -10000, max = 10000) int upper) {
    if (lower < upper) {
      Range<Integer> original = Range.openClosed(lower, upper);
      assertRoundtrip(original, Integer.class);
    }
  }

  @Property
  void integerAtLeastRangeRoundtrip(@ForAll @IntRange(min = -10000, max = 10000) int value) {
    Range<Integer> original = Range.atLeast(value);
    assertRoundtrip(original, Integer.class);
  }

  @Property
  void integerAtMostRangeRoundtrip(@ForAll @IntRange(min = -10000, max = 10000) int value) {
    Range<Integer> original = Range.atMost(value);
    assertRoundtrip(original, Integer.class);
  }

  @Property
  void integerGreaterThanRangeRoundtrip(@ForAll @IntRange(min = -10000, max = 10000) int value) {
    Range<Integer> original = Range.greaterThan(value);
    assertRoundtrip(original, Integer.class);
  }

  @Property
  void integerLessThanRangeRoundtrip(@ForAll @IntRange(min = -10000, max = 10000) int value) {
    Range<Integer> original = Range.lessThan(value);
    assertRoundtrip(original, Integer.class);
  }

  @Example
  void integerAllRangeRoundtrip() {
    Range<Integer> original = Range.all();
    assertRoundtrip(original, Integer.class);
  }

  // ==========================================================================
  // Long Ranges
  // ==========================================================================

  @Property
  void longClosedRangeRoundtrip(
      @ForAll @LongRange(min = -100000L, max = 100000L) long lower,
      @ForAll @LongRange(min = -100000L, max = 100000L) long upper) {
    if (lower <= upper) {
      Range<Long> original = Range.closed(lower, upper);
      assertRoundtrip(original, Long.class);
    }
  }

  @Property
  void longUnboundedRangeRoundtrip(@ForAll @LongRange(min = -100000L, max = 100000L) long value) {
    Range<Long> atLeast = Range.atLeast(value);
    Range<Long> atMost = Range.atMost(value);
    assertRoundtrip(atLeast, Long.class);
    assertRoundtrip(atMost, Long.class);
  }

  // ==========================================================================
  // Double Ranges
  // ==========================================================================

  @Property
  void doubleClosedRangeRoundtrip(
      @ForAll("finiteDoubles") double lower, @ForAll("finiteDoubles") double upper) {
    if (lower <= upper && Double.isFinite(lower) && Double.isFinite(upper)) {
      Range<Double> original = Range.closed(lower, upper);
      assertRoundtrip(original, Double.class);
    }
  }

  @Property
  void doubleUnboundedRangeRoundtrip(@ForAll("finiteDoubles") double value) {
    if (Double.isFinite(value)) {
      Range<Double> atLeast = Range.atLeast(value);
      Range<Double> atMost = Range.atMost(value);
      assertRoundtrip(atLeast, Double.class);
      assertRoundtrip(atMost, Double.class);
    }
  }

  @Provide
  Arbitrary<Double> finiteDoubles() {
    return Arbitraries.doubles().between(-1e6, 1e6).filter(Double::isFinite);
  }

  // ==========================================================================
  // BigInteger Ranges
  // ==========================================================================

  @Property
  void bigIntegerClosedRangeRoundtrip(
      @ForAll @IntRange(min = -10000, max = 10000) int lower,
      @ForAll @IntRange(min = -10000, max = 10000) int upper) {
    if (lower <= upper) {
      Range<BigInteger> original =
          Range.closed(BigInteger.valueOf(lower), BigInteger.valueOf(upper));
      assertRoundtrip(original, BigInteger.class);
    }
  }

  // ==========================================================================
  // BigDecimal Ranges
  // ==========================================================================

  @Property
  void bigDecimalClosedRangeRoundtrip(
      @ForAll("bigDecimals") BigDecimal lower, @ForAll("bigDecimals") BigDecimal upper) {
    if (lower.compareTo(upper) <= 0) {
      Range<BigDecimal> original = Range.closed(lower, upper);
      assertRoundtrip(original, BigDecimal.class);
    }
  }

  @Provide
  Arbitrary<BigDecimal> bigDecimals() {
    return Arbitraries.bigDecimals().between(new BigDecimal("-10000"), new BigDecimal("10000"));
  }

  // ==========================================================================
  // Duration Ranges
  // ==========================================================================

  @Property
  void durationClosedRangeRoundtrip(
      @ForAll("durations") Duration lower, @ForAll("durations") Duration upper) {
    if (lower.compareTo(upper) <= 0) {
      Range<Duration> original = Range.closed(lower, upper);
      assertRoundtrip(original, Duration.class);
    }
  }

  @Property
  void durationUnboundedRangeRoundtrip(@ForAll("durations") Duration value) {
    Range<Duration> atLeast = Range.atLeast(value);
    Range<Duration> atMost = Range.atMost(value);
    assertRoundtrip(atLeast, Duration.class);
    assertRoundtrip(atMost, Duration.class);
  }

  @Provide
  Arbitrary<Duration> durations() {
    return Arbitraries.longs()
        .between(0, 86400 * 365) // Up to 1 year in seconds
        .map(Duration::ofSeconds);
  }

  // ==========================================================================
  // LocalDate Ranges
  // ==========================================================================

  @Property
  void localDateClosedRangeRoundtrip(
      @ForAll("localDates") LocalDate lower, @ForAll("localDates") LocalDate upper) {
    if (!lower.isAfter(upper)) {
      Range<LocalDate> original = Range.closed(lower, upper);
      assertTemporalRoundtrip(original, LocalDate.class);
    }
  }

  @Provide
  Arbitrary<LocalDate> localDates() {
    return Arbitraries.longs()
        .between(0, 365 * 100) // 100 years of days
        .map(days -> LocalDate.of(2000, 1, 1).plusDays(days));
  }

  // ==========================================================================
  // LocalDateTime Ranges
  // ==========================================================================

  @Property
  void localDateTimeClosedRangeRoundtrip(
      @ForAll("localDateTimes") LocalDateTime lower,
      @ForAll("localDateTimes") LocalDateTime upper) {
    if (!lower.isAfter(upper)) {
      Range<LocalDateTime> original = Range.closed(lower, upper);
      assertTemporalRoundtrip(original, LocalDateTime.class);
    }
  }

  @Provide
  Arbitrary<LocalDateTime> localDateTimes() {
    return Combinators.combine(localDates(), localTimes()).as(LocalDate::atTime);
  }

  // ==========================================================================
  // LocalTime Ranges
  // ==========================================================================

  @Property
  void localTimeClosedRangeRoundtrip(
      @ForAll("localTimes") LocalTime lower, @ForAll("localTimes") LocalTime upper) {
    if (!lower.isAfter(upper)) {
      Range<LocalTime> original = Range.closed(lower, upper);
      assertRoundtrip(original, LocalTime.class);
    }
  }

  @Provide
  Arbitrary<LocalTime> localTimes() {
    return Arbitraries.integers()
        .between(0, 86399) // Seconds in a day
        .map(LocalTime::ofSecondOfDay);
  }

  // ==========================================================================
  // Instant Ranges
  // ==========================================================================

  @Property
  void instantClosedRangeRoundtrip(
      @ForAll("instants") Instant lower, @ForAll("instants") Instant upper) {
    if (!lower.isAfter(upper)) {
      Range<Instant> original = Range.closed(lower, upper);
      assertRoundtrip(original, Instant.class);
    }
  }

  @Provide
  Arbitrary<Instant> instants() {
    return Arbitraries.longs()
        .between(0, 86400L * 365 * 50) // 50 years in seconds from epoch
        .map(Instant::ofEpochSecond);
  }

  // ==========================================================================
  // String Ranges
  // ==========================================================================

  @Property
  void stringClosedRangeRoundtrip(
      @ForAll("simpleStrings") String lower, @ForAll("simpleStrings") String upper) {
    if (lower.compareTo(upper) <= 0) {
      Range<String> original = Range.closed(lower, upper);
      assertRoundtrip(original, String.class);
    }
  }

  @Provide
  Arbitrary<String> simpleStrings() {
    // Use simple alphanumeric strings to avoid parsing issues with special characters
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
  }

  // ==========================================================================
  // Helper Methods
  // ==========================================================================

  private <T extends Comparable<T>> void assertRoundtrip(Range<T> original, Class<T> type) {
    String formatted = formatter.format(original);
    Range<T> parsed = parser.parseRange(formatted, type);
    assertThat(parsed)
        .as("Roundtrip failed for: %s -> \"%s\" -> %s", original, formatted, parsed)
        .isEqualTo(original);
  }

  /**
   * Helper for temporal types that implement Comparable with a different type parameter (e.g.,
   * LocalDate implements Comparable<ChronoLocalDate>).
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void assertTemporalRoundtrip(Range original, Class type) {
    String formatted = formatter.format(original);
    Range parsed = parser.parseRange(formatted, type);
    assertThat((Object) parsed)
        .as("Roundtrip failed for: %s -> \"%s\" -> %s", original, formatted, parsed)
        .isEqualTo(original);
  }
}
