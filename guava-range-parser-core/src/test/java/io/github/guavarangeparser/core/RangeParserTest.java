package io.github.guavarangeparser.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Range;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RangeParserTest {

  @Nested
  class IntegerRanges {

    @Test
    void parseClosed() {
      Range<Integer> range = RangeParser.parse("[0..100]", Integer.class);
      assertThat(range).isEqualTo(Range.closed(0, 100));
    }

    @Test
    void parseOpen() {
      Range<Integer> range = RangeParser.parse("(0..100)", Integer.class);
      assertThat(range).isEqualTo(Range.open(0, 100));
    }

    @Test
    void parseClosedOpen() {
      Range<Integer> range = RangeParser.parse("[0..100)", Integer.class);
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void parseOpenClosed() {
      Range<Integer> range = RangeParser.parse("(0..100]", Integer.class);
      assertThat(range).isEqualTo(Range.openClosed(0, 100));
    }

    @Test
    void parseAtLeast() {
      Range<Integer> range = RangeParser.parse("[100..+∞)", Integer.class);
      assertThat(range).isEqualTo(Range.atLeast(100));
    }

    @Test
    void parseGreaterThan() {
      Range<Integer> range = RangeParser.parse("(100..+∞)", Integer.class);
      assertThat(range).isEqualTo(Range.greaterThan(100));
    }

    @Test
    void parseAtMost() {
      Range<Integer> range = RangeParser.parse("(-∞..100]", Integer.class);
      assertThat(range).isEqualTo(Range.atMost(100));
    }

    @Test
    void parseLessThan() {
      Range<Integer> range = RangeParser.parse("(-∞..100)", Integer.class);
      assertThat(range).isEqualTo(Range.lessThan(100));
    }

    @Test
    void parseAll() {
      Range<Integer> range = RangeParser.parse("(-∞..+∞)", Integer.class);
      assertThat(range).isEqualTo(Range.all());
    }

    @Test
    void parseNegativeNumbers() {
      Range<Integer> range = RangeParser.parse("[-100..-10)", Integer.class);
      assertThat(range).isEqualTo(Range.closedOpen(-100, -10));
    }
  }

  @Nested
  class InfinityVariants {

    @ParameterizedTest
    @ValueSource(strings = {"[0..+∞)", "[0..∞)", "[0..+inf)", "[0..inf)", "[0..+INF)", "[0..INF)", "[0..+Infinity)", "[0..Infinity)"})
    void parsePositiveInfinityVariants(String notation) {
      Range<Integer> range = RangeParser.parse(notation, Integer.class);
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"(-∞..100]", "(-inf..100]", "(-INF..100]", "(-Infinity..100]"})
    void parseNegativeInfinityVariants(String notation) {
      Range<Integer> range = RangeParser.parse(notation, Integer.class);
      assertThat(range).isEqualTo(Range.atMost(100));
    }
  }

  @Nested
  class DoubleRanges {

    @Test
    void parseClosedOpen() {
      Range<Double> range = RangeParser.parse("[0.0..1.0)", Double.class);
      assertThat(range).isEqualTo(Range.closedOpen(0.0, 1.0));
    }

    @Test
    void parseWithScientificNotation() {
      Range<Double> range = RangeParser.parse("[1e-10..1e10)", Double.class);
      assertThat(range).isEqualTo(Range.closedOpen(1e-10, 1e10));
    }
  }

  @Nested
  class DurationRanges {

    @Test
    void parseClosedOpen() {
      Range<Duration> range = RangeParser.parse("[PT0S..PT24H)", Duration.class);
      assertThat(range).isEqualTo(Range.closedOpen(Duration.ZERO, Duration.ofHours(24)));
    }

    @Test
    void parseAtLeast() {
      Range<Duration> range = RangeParser.parse("[PT1H..+∞)", Duration.class);
      assertThat(range).isEqualTo(Range.atLeast(Duration.ofHours(1)));
    }
  }

  @Nested
  class LocalDateRanges {

    @Test
    void parseClosedOpen() {
      Range<LocalDate> range = RangeParser.parse("[2024-01-01..2024-12-31]", LocalDate.class);
      assertThat(range).isEqualTo(Range.closed(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }
  }

  @Nested
  class BigDecimalRanges {

    @Test
    void parseClosedOpen() {
      Range<BigDecimal> range = RangeParser.parse("[0.00..100.00)", BigDecimal.class);
      assertThat(range).isEqualTo(Range.closedOpen(new BigDecimal("0.00"), new BigDecimal("100.00")));
    }
  }

  @Nested
  class LenientMode {

    @Test
    void parseBracketlessNotation() {
      RangeParser parser = RangeParser.builder().lenient(true).build();
      Range<Integer> range = parser.parseRange("0..100", Integer.class);
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }
  }

  @Nested
  class ErrorHandling {

    @Test
    void throwsOnEmptyString() {
      assertThatThrownBy(() -> RangeParser.parse("", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("cannot be empty");
    }

    @Test
    void throwsOnNullString() {
      assertThatThrownBy(() -> RangeParser.parse(null, Integer.class))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsOnInvalidFormat() {
      assertThatThrownBy(() -> RangeParser.parse("0-100", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Invalid range format");
    }

    @Test
    void throwsOnUnsupportedType() {
      assertThatThrownBy(() -> RangeParser.parse("[a..z]", StringBuilder.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("No type adapter registered");
    }

    @Test
    void throwsOnInvalidNumber() {
      assertThatThrownBy(() -> RangeParser.parse("[abc..100)", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Failed to parse");
    }
  }

  @Nested
  class CustomTypeAdapter {

    @Test
    void parseWithCustomAdapter() {
      record Money(int cents) implements Comparable<Money> {
        static Money parse(String value) {
          return new Money(Integer.parseInt(value.replace("$", "")) * 100);
        }

        @Override
        public int compareTo(Money other) {
          return Integer.compare(this.cents, other.cents);
        }
      }

      RangeParser parser =
          RangeParser.builder().registerType(Money.class, Money::parse).build();

      Range<Money> range = parser.parseRange("[$10..$100)", Money.class);
      assertThat(range).isEqualTo(Range.closedOpen(new Money(1000), new Money(10000)));
    }
  }

  @Nested
  class Whitespace {

    @Test
    void trimsWhitespace() {
      Range<Integer> range = RangeParser.parse("  [0..100)  ", Integer.class);
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void handlesWhitespaceAroundEndpoints() {
      Range<Integer> range = RangeParser.parse("[ 0 .. 100 )", Integer.class);
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }
  }
}