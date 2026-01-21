package io.github.neewrobert.guavarangeparser.core;

/**
 * Defines the style for representing infinity values in range notation.
 *
 * <p>When formatting ranges with unbounded endpoints, this enum determines how infinity is
 * represented in the output string.
 *
 * @see RangeFormatter
 */
public enum InfinityStyle {

  /**
   * Mathematical symbol style: {@code +∞} and {@code -∞}.
   *
   * <p>This is the most compact and mathematically standard representation.
   */
  SYMBOL("+∞", "-∞"),

  /**
   * Lowercase word style: {@code +inf} and {@code -inf}.
   *
   * <p>ASCII-safe representation commonly used in programming.
   */
  WORD_LOWER("+inf", "-inf"),

  /**
   * Uppercase word style: {@code +INF} and {@code -INF}.
   *
   * <p>ASCII-safe representation, uppercase variant.
   */
  WORD_UPPER("+INF", "-INF"),

  /**
   * Full word style: {@code +Infinity} and {@code -Infinity}.
   *
   * <p>Most verbose but most readable representation.
   */
  WORD_FULL("+Infinity", "-Infinity");

  private final String positiveInfinity;
  private final String negativeInfinity;

  InfinityStyle(String positiveInfinity, String negativeInfinity) {
    this.positiveInfinity = positiveInfinity;
    this.negativeInfinity = negativeInfinity;
  }

  /**
   * Returns the string representation of positive infinity.
   *
   * @return the positive infinity string
   */
  public String positiveInfinity() {
    return positiveInfinity;
  }

  /**
   * Returns the string representation of negative infinity.
   *
   * @return the negative infinity string
   */
  public String negativeInfinity() {
    return negativeInfinity;
  }
}
