package io.github.neewrobert.guavarangeparser.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Range;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
    @ValueSource(
        strings = {
          "[0..+∞)",
          "[0..∞)",
          "[0..+inf)",
          "[0..inf)",
          "[0..+INF)",
          "[0..INF)",
          "[0..+Infinity)",
          "[0..Infinity)"
        })
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
  class LongRanges {

    @Test
    void parseClosed() {
      Range<Long> range = RangeParser.parse("[0..9999999999]", Long.class);
      assertThat(range).isEqualTo(Range.closed(0L, 9999999999L));
    }

    @Test
    void parseAtLeast() {
      Range<Long> range = RangeParser.parse("[1000000000..+∞)", Long.class);
      assertThat(range).isEqualTo(Range.atLeast(1000000000L));
    }

    @Test
    void parseNegativeValues() {
      Range<Long> range = RangeParser.parse("[-9999999999..-1]", Long.class);
      assertThat(range).isEqualTo(Range.closed(-9999999999L, -1L));
    }
  }

  @Nested
  class StringRanges {

    @Test
    void parseClosed() {
      Range<String> range = RangeParser.parse("[a..z]", String.class);
      assertThat(range).isEqualTo(Range.closed("a", "z"));
    }

    @Test
    void parseAtLeast() {
      Range<String> range = RangeParser.parse("[m..+∞)", String.class);
      assertThat(range).isEqualTo(Range.atLeast("m"));
    }

    @Test
    void parseAtMost() {
      Range<String> range = RangeParser.parse("(-∞..m]", String.class);
      assertThat(range).isEqualTo(Range.atMost("m"));
    }

    @Test
    void parseMultiCharacterStrings() {
      Range<String> range = RangeParser.parse("[apple..zebra]", String.class);
      assertThat(range).isEqualTo(Range.closed("apple", "zebra"));
    }
  }

  @Nested
  class CharacterRanges {

    @Test
    void parseClosed() {
      Range<Character> range = RangeParser.parse("[a..z]", Character.class);
      assertThat(range).isEqualTo(Range.closed('a', 'z'));
    }

    @Test
    void parseOpen() {
      Range<Character> range = RangeParser.parse("(a..z)", Character.class);
      assertThat(range).isEqualTo(Range.open('a', 'z'));
    }

    @Test
    void parseClosedOpen() {
      Range<Character> range = RangeParser.parse("[a..z)", Character.class);
      assertThat(range).isEqualTo(Range.closedOpen('a', 'z'));
    }

    @Test
    void parseOpenClosed() {
      Range<Character> range = RangeParser.parse("(a..z]", Character.class);
      assertThat(range).isEqualTo(Range.openClosed('a', 'z'));
    }

    @Test
    void parseNumericCharacters() {
      Range<Character> range = RangeParser.parse("[0..9]", Character.class);
      assertThat(range).isEqualTo(Range.closed('0', '9'));
    }

    @Test
    void parseSpecialCharacters() {
      Range<Character> range = RangeParser.parse("[!..?]", Character.class);
      assertThat(range).isEqualTo(Range.closed('!', '?'));
    }

    @Test
    void parseAtLeast() {
      Range<Character> range = RangeParser.parse("[a..+∞)", Character.class);
      assertThat(range).isEqualTo(Range.atLeast('a'));
    }

    @Test
    void parseAtMost() {
      Range<Character> range = RangeParser.parse("(-∞..z]", Character.class);
      assertThat(range).isEqualTo(Range.atMost('z'));
    }

    @Test
    void throwsOnMultiCharacterString() {
      assertThatThrownBy(() -> RangeParser.parse("[ab..xy]", Character.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Expected single character but got: 'ab'");
    }

    @Test
    void throwsOnMultiCharacterUpperBound() {
      assertThatThrownBy(() -> RangeParser.parse("[a..xyz]", Character.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Expected single character but got: 'xyz'");
    }

    @Test
    void characterAdapterThrowsOnEmptyString() {
      // Test the CHARACTER TypeAdapter directly for edge cases
      assertThatThrownBy(() -> BuiltInTypeAdapters.CHARACTER.parse(""))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Expected single character but got: ''");
    }

    @Test
    void characterAdapterThrowsOnMultiCharacterString() {
      assertThatThrownBy(() -> BuiltInTypeAdapters.CHARACTER.parse("abc"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Expected single character but got: 'abc'");
    }

    @Test
    void characterAdapterParsesValidSingleCharacter() {
      assertThat(BuiltInTypeAdapters.CHARACTER.parse("a")).isEqualTo('a');
      assertThat(BuiltInTypeAdapters.CHARACTER.parse("Z")).isEqualTo('Z');
      assertThat(BuiltInTypeAdapters.CHARACTER.parse("0")).isEqualTo('0');
      assertThat(BuiltInTypeAdapters.CHARACTER.parse("!")).isEqualTo('!');
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
    void parseOpenClosed() {
      Range<Double> range = RangeParser.parse("(0.1..1.0]", Double.class);
      assertThat(range).isEqualTo(Range.openClosed(0.1, 1.0));
    }

    @Test
    void parseWithScientificNotation() {
      Range<Double> range = RangeParser.parse("[1e-10..1e10)", Double.class);
      assertThat(range).isEqualTo(Range.closedOpen(1e-10, 1e10));
    }

    @Test
    void parseAtLeast() {
      Range<Double> range = RangeParser.parse("[0.5..+∞)", Double.class);
      assertThat(range).isEqualTo(Range.atLeast(0.5));
    }

    @Test
    void parseAtMost() {
      Range<Double> range = RangeParser.parse("(-∞..99.9]", Double.class);
      assertThat(range).isEqualTo(Range.atMost(99.9));
    }

    @Test
    void parseAll() {
      Range<Double> range = RangeParser.parse("(-∞..+∞)", Double.class);
      assertThat(range).isEqualTo(Range.all());
    }

    @Test
    void parseNegativeValues() {
      Range<Double> range = RangeParser.parse("[-10.5..-0.5)", Double.class);
      assertThat(range).isEqualTo(Range.closedOpen(-10.5, -0.5));
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

    @Test
    void parseAtMost() {
      Range<Duration> range = RangeParser.parse("(-∞..PT12H]", Duration.class);
      assertThat(range).isEqualTo(Range.atMost(Duration.ofHours(12)));
    }

    @Test
    void parseAll() {
      Range<Duration> range = RangeParser.parse("(-∞..+∞)", Duration.class);
      assertThat(range).isEqualTo(Range.all());
    }

    @Test
    void parseNegativeDuration() {
      Range<Duration> range = RangeParser.parse("[PT-1H..PT1H]", Duration.class);
      assertThat(range).isEqualTo(Range.closed(Duration.ofHours(-1), Duration.ofHours(1)));
    }
  }

  @Nested
  class LocalDateRanges {

    @Test
    void parseClosed() {
      Range<LocalDate> range = RangeParser.parse("[2024-01-01..2024-12-31]", LocalDate.class);
      assertThat(range)
          .isEqualTo(Range.closed(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }

    @Test
    void parseClosedOpen() {
      Range<LocalDate> range = RangeParser.parse("[2024-01-01..2024-12-31)", LocalDate.class);
      assertThat(range)
          .isEqualTo(Range.closedOpen(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }

    @Test
    void parseAtLeast() {
      Range<LocalDate> range = RangeParser.parse("[2024-01-01..+∞)", LocalDate.class);
      assertThat(range).isEqualTo(Range.atLeast(LocalDate.of(2024, 1, 1)));
    }

    @Test
    void parseAtMost() {
      Range<LocalDate> range = RangeParser.parse("(-∞..2024-12-31]", LocalDate.class);
      assertThat(range).isEqualTo(Range.atMost(LocalDate.of(2024, 12, 31)));
    }

    @Test
    void parseAll() {
      Range<LocalDate> range = RangeParser.parse("(-∞..+∞)", LocalDate.class);
      assertThat(range).isEqualTo(Range.all());
    }
  }

  @Nested
  class BigDecimalRanges {

    @Test
    void parseClosedOpen() {
      Range<BigDecimal> range = RangeParser.parse("[0.00..100.00)", BigDecimal.class);
      assertThat(range)
          .isEqualTo(Range.closedOpen(new BigDecimal("0.00"), new BigDecimal("100.00")));
    }

    @Test
    void parseAtLeast() {
      Range<BigDecimal> range = RangeParser.parse("[0.01..+∞)", BigDecimal.class);
      assertThat(range).isEqualTo(Range.atLeast(new BigDecimal("0.01")));
    }

    @Test
    void parseAtMost() {
      Range<BigDecimal> range = RangeParser.parse("(-∞..999.99]", BigDecimal.class);
      assertThat(range).isEqualTo(Range.atMost(new BigDecimal("999.99")));
    }

    @Test
    void parseAll() {
      Range<BigDecimal> range = RangeParser.parse("(-∞..+∞)", BigDecimal.class);
      assertThat(range).isEqualTo(Range.all());
    }

    @Test
    void parseNegativeValues() {
      Range<BigDecimal> range = RangeParser.parse("[-100.50..-0.01]", BigDecimal.class);
      assertThat(range).isEqualTo(Range.closed(new BigDecimal("-100.50"), new BigDecimal("-0.01")));
    }

    @Test
    void parseHighPrecision() {
      Range<BigDecimal> range = RangeParser.parse("[0.123456789..0.987654321)", BigDecimal.class);
      assertThat(range)
          .isEqualTo(
              Range.closedOpen(new BigDecimal("0.123456789"), new BigDecimal("0.987654321")));
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

    @Test
    void lenientModeIgnoresInputWithBrackets() {
      // Test the branch where lenient=true but input already has brackets
      RangeParser parser = RangeParser.builder().lenient(true).build();
      Range<Integer> range = parser.parseRange("[0..100]", Integer.class);
      assertThat(range).isEqualTo(Range.closed(0, 100));
    }

    @Test
    void lenientModeIgnoresInputWithParentheses() {
      RangeParser parser = RangeParser.builder().lenient(true).build();
      Range<Integer> range = parser.parseRange("(0..100)", Integer.class);
      assertThat(range).isEqualTo(Range.open(0, 100));
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
    void throwsOnTooShortInput() {
      assertThatThrownBy(() -> RangeParser.parse("[1..", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Invalid range format");
    }

    @Test
    void throwsOnInvalidClosingBracket() {
      assertThatThrownBy(() -> RangeParser.parse("[0..100}", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Invalid range format");
    }

    @Test
    void throwsOnEmptyLowerBound() {
      // This creates a case where lowerPart is empty after trimming
      RangeParser parser = RangeParser.builder().lenient(false).build();
      assertThatThrownBy(() -> parser.parseRange("[  ..100]", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Invalid range format");
    }

    @Test
    void throwsOnEmptyUpperBound() {
      // This creates a case where upperPart is empty after trimming
      RangeParser parser = RangeParser.builder().lenient(false).build();
      assertThatThrownBy(() -> parser.parseRange("[0..  ]", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Invalid range format");
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

    @Test
    void throwsOnInputExceedingMaxLength() {
      String longInput = "[" + "1".repeat(1500) + "..100)";
      assertThatThrownBy(() -> RangeParser.parse(longInput, Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("exceeds maximum length");
    }

    @Test
    void acceptsInputAtExactlyMaxLength() {
      // Create a valid range string at exactly 1000 chars (the max limit)
      // Format: [<digits>..<digits>] = 1 + digits + 2 + digits + 1 = 4 + 2*digits
      // For 1000 chars: digits = (1000 - 4) / 2 = 498
      String digits = "1".repeat(498);
      String input = "[" + digits + ".." + digits + "]";
      assertThat(input.length()).isEqualTo(1000);

      // Should NOT throw length exception - fails later on number parsing
      assertThatThrownBy(() -> RangeParser.parse(input, Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Failed to parse")
          .hasMessageNotContaining("exceeds maximum length");
    }

    @Test
    void rejectsInputAtOneOverMaxLength() {
      // Create input at exactly 1001 chars (one over the limit)
      String digits = "1".repeat(498);
      String input = "[" + digits + ".." + digits + "]!"; // 1001 chars
      assertThat(input.length()).isEqualTo(1001);

      assertThatThrownBy(() -> RangeParser.parse(input, Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("exceeds maximum length");
    }

    @Test
    void rejectsInputWithMissingSeparator() {
      // Input that could cause ReDoS with a vulnerable regex
      String maliciousInput = "[" + "a".repeat(100) + "]";
      assertThatThrownBy(() -> RangeParser.parse(maliciousInput, String.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Invalid range format");
    }

    @Test
    void rejectsInputWithManyDotsButNoSeparator() {
      // Input with many single dots that could confuse a greedy regex
      String maliciousInput = "[" + "a.".repeat(50) + "b]";
      assertThatThrownBy(() -> RangeParser.parse(maliciousInput, String.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Invalid range format");
    }

    @Test
    void handlesLongInputWithValidFormatQuickly() {
      // Long but valid input should be processed quickly (no exponential backtracking)
      String longValue = "a".repeat(400);
      String input = "[" + longValue + ".." + longValue + "]";

      long start = System.nanoTime();
      assertThatThrownBy(() -> RangeParser.parse(input, Integer.class))
          .isInstanceOf(RangeParseException.class);
      long elapsedMs = (System.nanoTime() - start) / 1_000_000;

      // Should complete in under 100ms (ReDoS would take seconds or minutes)
      assertThat(elapsedMs).isLessThan(100);
    }

    @Test
    void rejectsClosedLowerBoundAtNegativeInfinity() {
      assertThatThrownBy(() -> RangeParser.parse("[-∞..100]", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("negative infinity bound must be open");
    }

    @Test
    void rejectsClosedUpperBoundAtPositiveInfinity() {
      assertThatThrownBy(() -> RangeParser.parse("[0..+∞]", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("positive infinity bound must be open");
    }

    @Test
    void rejectsLowerBoundGreaterThanUpperBound() {
      assertThatThrownBy(() -> RangeParser.parse("[100..0]", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("lower bound")
          .hasMessageContaining("greater than upper bound");
    }

    @Test
    void rejectsNullFromTypeAdapter() {
      TypeAdapter<Integer> nullAdapter = s -> null;
      RangeParser parser = RangeParser.builder().registerType(Integer.class, nullAdapter).build();

      assertThatThrownBy(() -> parser.parseRange("[0..100]", Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("TypeAdapter returned null");
    }
  }

  @Nested
  class RangeParseExceptionDetails {

    @Test
    void exceptionContainsInputString() {
      String input = "invalid-range";
      try {
        RangeParser.parse(input, Integer.class);
      } catch (RangeParseException e) {
        assertThat(e.getInput()).isEqualTo(input);
      }
    }

    @Test
    void exceptionContainsPosition() {
      String input = "invalid-range";
      try {
        RangeParser.parse(input, Integer.class);
      } catch (RangeParseException e) {
        assertThat(e.getPosition()).isEqualTo(0);
      }
    }

    @Test
    void exceptionContainsNonZeroPosition() {
      // Directly create exception with non-zero position to verify getPosition() returns it
      RangeParseException ex = new RangeParseException("Error", "input", 5);
      assertThat(ex.getPosition()).isEqualTo(5);
    }

    @Test
    void exceptionMessageContainsInputInQuotes() {
      String input = "[invalid..range)";
      assertThatThrownBy(() -> RangeParser.parse(input, Integer.class))
          .isInstanceOf(RangeParseException.class)
          .hasMessageContaining("Input: \"" + input + "\"");
    }

    @Test
    void exceptionWithPositionShowsCaretPointer() {
      // Create exception with position in the middle of input
      RangeParseException ex = new RangeParseException("Error at position", "abcdefgh", 3);
      String message = ex.getMessage();
      assertThat(message).contains("^");
      assertThat(message).contains("   ^"); // 3 spaces before caret
    }

    @Test
    void exceptionWithZeroPositionDoesNotShowCaret() {
      // When position is 0, no caret should be shown
      RangeParseException ex = new RangeParseException("Error", "input", 0);
      String message = ex.getMessage();
      assertThat(message).doesNotContain("^");
    }

    @Test
    void exceptionWithPositionAtEndDoesNotShowCaret() {
      // When position >= input.length(), no caret should be shown
      String input = "test";
      RangeParseException ex = new RangeParseException("Error", input, input.length());
      String message = ex.getMessage();
      assertThat(message).doesNotContain("^");
    }

    @Test
    void exceptionWithCausePreservesCause() {
      IllegalArgumentException cause = new IllegalArgumentException("root cause");
      RangeParseException ex = new RangeParseException("Error", "input", 0, cause);
      assertThat(ex.getCause()).isSameAs(cause);
      assertThat(ex.getInput()).isEqualTo("input");
      assertThat(ex.getPosition()).isEqualTo(0);
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

      RangeParser parser = RangeParser.builder().registerType(Money.class, Money::parse).build();

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

  @Nested
  class BuilderReuse {

    @Test
    void builderCanBeReusedMultipleTimes() {
      RangeParser.Builder builder = RangeParser.builder().lenient(true);
      RangeParser p1 = builder.build();
      RangeParser p2 = builder.build();

      // Both parsers should have built-in adapters
      Range<Integer> r1 = p1.parseRange("[0..100)", Integer.class);
      Range<Integer> r2 = p2.parseRange("[0..100)", Integer.class);

      assertThat(r1).isEqualTo(Range.closedOpen(0, 100));
      assertThat(r2).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void builderReusedWithCustomAdapters() {
      record Point(int x) implements Comparable<Point> {
        static Point parse(String s) {
          return new Point(Integer.parseInt(s));
        }

        @Override
        public int compareTo(Point other) {
          return Integer.compare(this.x, other.x);
        }
      }

      RangeParser.Builder builder = RangeParser.builder().registerType(Point.class, Point::parse);
      RangeParser p1 = builder.build();
      RangeParser p2 = builder.build();

      // Both parsers should work with custom AND built-in types
      Range<Point> r1 = p1.parseRange("[0..100)", Point.class);
      Range<Integer> r2 = p2.parseRange("[0..100)", Integer.class);
      Range<Point> r3 = p2.parseRange("[50..200]", Point.class);

      assertThat(r1).isEqualTo(Range.closedOpen(new Point(0), new Point(100)));
      assertThat(r2).isEqualTo(Range.closedOpen(0, 100));
      assertThat(r3).isEqualTo(Range.closed(new Point(50), new Point(200)));
    }

    @Test
    void customAdapterCanOverrideBuiltIn() {
      // Custom Integer adapter that doubles the value
      TypeAdapter<Integer> doublingAdapter = s -> Integer.parseInt(s) * 2;

      RangeParser parser =
          RangeParser.builder().registerType(Integer.class, doublingAdapter).build();

      // If override works, [5..10) should become [10..20)
      Range<Integer> range = parser.parseRange("[5..10)", Integer.class);
      assertThat(range).isEqualTo(Range.closedOpen(10, 20));
    }
  }
}
