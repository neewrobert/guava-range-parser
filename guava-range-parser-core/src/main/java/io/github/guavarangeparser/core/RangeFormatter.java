package io.github.guavarangeparser.core;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.util.Objects;

/**
 * Formatter for converting Guava {@link Range} objects to string notation.
 *
 * <p>Produces standard mathematical interval notation:
 *
 * <ul>
 *   <li>{@code [a..b]} - closed range
 *   <li>{@code (a..b)} - open range
 *   <li>{@code [a..b)} - closed-open range
 *   <li>{@code (a..b]} - open-closed range
 *   <li>{@code [a..+∞)} - at least
 *   <li>{@code (a..+∞)} - greater than
 *   <li>{@code (-∞..b]} - at most
 *   <li>{@code (-∞..b)} - less than
 *   <li>{@code (-∞..+∞)} - all
 * </ul>
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * String notation = RangeFormatter.toString(Range.closedOpen(0, 100));
 * // Returns: "[0..100)"
 * }</pre>
 *
 * @see Range
 * @see RangeParser
 */
public final class RangeFormatter {

  private final InfinityStyle infinityStyle;

  private RangeFormatter(Builder builder) {
    this.infinityStyle = builder.infinityStyle;
  }

  /**
   * Creates a new builder for configuring a RangeFormatter instance.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Formats a Range to string notation using default settings.
   *
   * @param range the range to format
   * @param <T> the type of the range elements
   * @return the string notation
   */
  public static <T extends Comparable<T>> String toString(Range<T> range) {
    return builder().build().format(range);
  }

  /**
   * Formats a Range to string notation.
   *
   * @param range the range to format
   * @param <T> the type of the range elements
   * @return the string notation
   */
  public <T extends Comparable<T>> String format(Range<T> range) {
    Objects.requireNonNull(range, "range must not be null");

    StringBuilder sb = new StringBuilder();

    // Opening bracket
    if (range.hasLowerBound()) {
      sb.append(range.lowerBoundType() == BoundType.CLOSED ? "[" : "(");
      sb.append(range.lowerEndpoint());
    } else {
      sb.append("(");
      sb.append(infinityStyle.negativeInfinity());
    }

    sb.append("..");

    // Closing bracket
    if (range.hasUpperBound()) {
      sb.append(range.upperEndpoint());
      sb.append(range.upperBoundType() == BoundType.CLOSED ? "]" : ")");
    } else {
      sb.append(infinityStyle.positiveInfinity());
      sb.append(")");
    }

    return sb.toString();
  }

  /**
   * Builder for creating configured {@link RangeFormatter} instances.
   */
  public static final class Builder {
    private InfinityStyle infinityStyle = InfinityStyle.SYMBOL;

    private Builder() {}

    /**
     * Sets the style for representing infinity in output.
     *
     * @param infinityStyle the infinity style to use
     * @return this builder
     */
    public Builder infinityStyle(InfinityStyle infinityStyle) {
      this.infinityStyle = Objects.requireNonNull(infinityStyle);
      return this;
    }

    /**
     * Builds the configured RangeFormatter.
     *
     * @return a new RangeFormatter instance
     */
    public RangeFormatter build() {
      return new RangeFormatter(this);
    }
  }
}