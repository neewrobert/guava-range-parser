package io.github.guavarangeparser.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Range;
import java.time.Duration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RangeFormatterTest {

  @Nested
  class BoundedRanges {

    @Test
    void formatClosed() {
      String notation = RangeFormatter.toString(Range.closed(0, 100));
      assertThat(notation).isEqualTo("[0..100]");
    }

    @Test
    void formatOpen() {
      String notation = RangeFormatter.toString(Range.open(0, 100));
      assertThat(notation).isEqualTo("(0..100)");
    }

    @Test
    void formatClosedOpen() {
      String notation = RangeFormatter.toString(Range.closedOpen(0, 100));
      assertThat(notation).isEqualTo("[0..100)");
    }

    @Test
    void formatOpenClosed() {
      String notation = RangeFormatter.toString(Range.openClosed(0, 100));
      assertThat(notation).isEqualTo("(0..100]");
    }
  }

  @Nested
  class UnboundedRanges {

    @Test
    void formatAtLeast() {
      String notation = RangeFormatter.toString(Range.atLeast(100));
      assertThat(notation).isEqualTo("[100..+∞)");
    }

    @Test
    void formatGreaterThan() {
      String notation = RangeFormatter.toString(Range.greaterThan(100));
      assertThat(notation).isEqualTo("(100..+∞)");
    }

    @Test
    void formatAtMost() {
      String notation = RangeFormatter.toString(Range.atMost(100));
      assertThat(notation).isEqualTo("(-∞..100]");
    }

    @Test
    void formatLessThan() {
      String notation = RangeFormatter.toString(Range.lessThan(100));
      assertThat(notation).isEqualTo("(-∞..100)");
    }

    @Test
    void formatAll() {
      String notation = RangeFormatter.toString(Range.<Integer>all());
      assertThat(notation).isEqualTo("(-∞..+∞)");
    }
  }

  @Nested
  class InfinityStyles {

    @Test
    void symbolStyle() {
      RangeFormatter formatter =
          RangeFormatter.builder().infinityStyle(InfinityStyle.SYMBOL).build();
      String notation = formatter.format(Range.atLeast(0));
      assertThat(notation).isEqualTo("[0..+∞)");
    }

    @Test
    void wordLowerStyle() {
      RangeFormatter formatter =
          RangeFormatter.builder().infinityStyle(InfinityStyle.WORD_LOWER).build();
      String notation = formatter.format(Range.atLeast(0));
      assertThat(notation).isEqualTo("[0..+inf)");
    }

    @Test
    void wordUpperStyle() {
      RangeFormatter formatter =
          RangeFormatter.builder().infinityStyle(InfinityStyle.WORD_UPPER).build();
      String notation = formatter.format(Range.atLeast(0));
      assertThat(notation).isEqualTo("[0..+INF)");
    }

    @Test
    void wordFullStyle() {
      RangeFormatter formatter =
          RangeFormatter.builder().infinityStyle(InfinityStyle.WORD_FULL).build();
      String notation = formatter.format(Range.atLeast(0));
      assertThat(notation).isEqualTo("[0..+Infinity)");
    }

    @Test
    void negativeInfinityWithStyle() {
      RangeFormatter formatter =
          RangeFormatter.builder().infinityStyle(InfinityStyle.WORD_UPPER).build();
      String notation = formatter.format(Range.lessThan(100));
      assertThat(notation).isEqualTo("(-INF..100)");
    }
  }

  @Nested
  class DurationRanges {

    @Test
    void formatDurationRange() {
      String notation =
          RangeFormatter.toString(Range.closedOpen(Duration.ZERO, Duration.ofHours(24)));
      assertThat(notation).isEqualTo("[PT0S..PT24H)");
    }

    @Test
    void formatUnboundedDuration() {
      String notation = RangeFormatter.toString(Range.atLeast(Duration.ofHours(1)));
      assertThat(notation).isEqualTo("[PT1H..+∞)");
    }
  }

  @Nested
  class RoundTrip {

    @Test
    void roundTripClosedOpen() {
      Range<Integer> original = Range.closedOpen(0, 100);
      String notation = RangeFormatter.toString(original);
      Range<Integer> parsed = RangeParser.parse(notation, Integer.class);
      assertThat(parsed).isEqualTo(original);
    }

    @Test
    void roundTripAtLeast() {
      Range<Integer> original = Range.atLeast(100);
      String notation = RangeFormatter.toString(original);
      Range<Integer> parsed = RangeParser.parse(notation, Integer.class);
      assertThat(parsed).isEqualTo(original);
    }

    @Test
    void roundTripAll() {
      Range<Integer> original = Range.all();
      String notation = RangeFormatter.toString(original);
      Range<Integer> parsed = RangeParser.parse(notation, Integer.class);
      assertThat(parsed).isEqualTo(original);
    }

    @Test
    void roundTripDuration() {
      Range<Duration> original = Range.closedOpen(Duration.ZERO, Duration.ofHours(24));
      String notation = RangeFormatter.toString(original);
      Range<Duration> parsed = RangeParser.parse(notation, Duration.class);
      assertThat(parsed).isEqualTo(original);
    }
  }

  @Nested
  class ErrorHandling {

    @Test
    void throwsOnNullRange() {
      assertThatThrownBy(() -> RangeFormatter.toString(null))
          .isInstanceOf(NullPointerException.class);
    }
  }
}
