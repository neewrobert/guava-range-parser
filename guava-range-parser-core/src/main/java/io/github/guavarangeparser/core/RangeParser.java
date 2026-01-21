package io.github.guavarangeparser.core;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for converting string notation to Guava {@link Range} objects.
 *
 * <p>Supports all 9 Guava Range types using standard mathematical interval notation:
 *
 * <ul>
 *   <li>{@code [a..b]} - closed range (both endpoints inclusive)
 *   <li>{@code (a..b)} - open range (both endpoints exclusive)
 *   <li>{@code [a..b)} - closed-open range
 *   <li>{@code (a..b]} - open-closed range
 *   <li>{@code [a..+∞)} - at least (lower bounded)
 *   <li>{@code (a..+∞)} - greater than
 *   <li>{@code (-∞..b]} - at most (upper bounded)
 *   <li>{@code (-∞..b)} - less than
 *   <li>{@code (-∞..+∞)} - all (unbounded)
 * </ul>
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * Range<Integer> range = RangeParser.parse("[0..100)", Integer.class);
 * Range<Double> range = RangeParser.parse("(0.0..1.0]", Double.class);
 * }</pre>
 *
 * @see Range
 * @see RangeFormatter
 */
public final class RangeParser {

  /** Pattern for positive infinity representations. */
  private static final String POS_INF_PATTERN = "(?:\\+∞|∞|\\+inf|inf|\\+INF|INF|\\+Infinity|Infinity)";

  /** Pattern for negative infinity representations. */
  private static final String NEG_INF_PATTERN = "(?:-∞|-inf|-INF|-Infinity)";

  /**
   * Main pattern to parse range notation.
   *
   * <p>Groups:
   * <ol>
   *   <li>Opening bracket: '[' or '('
   *   <li>Lower bound: value or negative infinity
   *   <li>Upper bound: value or positive infinity
   *   <li>Closing bracket: ']' or ')'
   * </ol>
   *
   * <p>The pattern uses a non-greedy match for values and requires the ".." separator.
   * Values can contain single dots (for decimals like "1.5") but not double dots.
   */
  private static final Pattern RANGE_PATTERN =
      Pattern.compile(
          "^([\\[(])"
              + "(" + NEG_INF_PATTERN + "|.+?)"
              + "\\.\\."
              + "(" + POS_INF_PATTERN + "|.+?)"
              + "([\\])])$");

  /** Pattern to detect negative infinity. */
  private static final Pattern NEG_INF_DETECT = Pattern.compile("^" + NEG_INF_PATTERN + "$");

  /** Pattern to detect positive infinity. */
  private static final Pattern POS_INF_DETECT = Pattern.compile("^" + POS_INF_PATTERN + "$");

  private final Map<Class<?>, TypeAdapter<?>> typeAdapters;
  private final boolean lenient;

  private RangeParser(Builder builder) {
    this.typeAdapters = new HashMap<>(builder.typeAdapters);
    this.lenient = builder.lenient;
    // Register built-in adapters
    BuiltInTypeAdapters.registerAll(this.typeAdapters);
  }

  /**
   * Creates a new builder for configuring a RangeParser instance.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Parses a range string using default settings.
   *
   * @param rangeString the string to parse (e.g., "[0..100)")
   * @param elementType the class of the range elements
   * @param <T> the type of the range elements (must be Comparable)
   * @return the parsed Range
   * @throws RangeParseException if the string cannot be parsed
   */
  public static <T extends Comparable<?>> Range<T> parse(String rangeString, Class<T> elementType) {
    return builder().build().parseRange(rangeString, elementType);
  }

  /**
   * Parses a range string into a Range object.
   *
   * @param rangeString the string to parse (e.g., "[0..100)")
   * @param elementType the class of the range elements
   * @param <T> the type of the range elements (must be Comparable)
   * @return the parsed Range
   * @throws RangeParseException if the string cannot be parsed
   */
  @SuppressWarnings("unchecked")
  public <T extends Comparable<?>> Range<T> parseRange(String rangeString, Class<T> elementType) {
    Objects.requireNonNull(rangeString, "rangeString must not be null");
    Objects.requireNonNull(elementType, "elementType must not be null");

    String trimmed = rangeString.trim();
    if (trimmed.isEmpty()) {
      throw new RangeParseException("Range string cannot be empty", rangeString, 0);
    }

    // Handle lenient mode for bracket-less notation
    if (lenient && !trimmed.startsWith("[") && !trimmed.startsWith("(")) {
      trimmed = "[" + trimmed + ")";
    }

    Matcher matcher = RANGE_PATTERN.matcher(trimmed);
    if (!matcher.matches()) {
      throw new RangeParseException(
          "Invalid range format. Expected notation like '[a..b)', '(a..b]', '(-∞..+∞)', etc.",
          rangeString,
          0);
    }

    String openingBracket = matcher.group(1);
    String lowerPart = matcher.group(2).trim();
    String upperPart = matcher.group(3).trim();
    String closingBracket = matcher.group(4);

    BoundType lowerBoundType = "[".equals(openingBracket) ? BoundType.CLOSED : BoundType.OPEN;
    BoundType upperBoundType = "]".equals(closingBracket) ? BoundType.CLOSED : BoundType.OPEN;

    boolean lowerUnbounded = NEG_INF_DETECT.matcher(lowerPart).matches();
    boolean upperUnbounded = POS_INF_DETECT.matcher(upperPart).matches();

    TypeAdapter<T> adapter = (TypeAdapter<T>) typeAdapters.get(elementType);
    if (adapter == null) {
      throw new RangeParseException(
          "No type adapter registered for: " + elementType.getName(), rangeString, 0);
    }

    try {
      return buildRange(
          lowerPart,
          upperPart,
          lowerBoundType,
          upperBoundType,
          lowerUnbounded,
          upperUnbounded,
          adapter);
    } catch (RangeParseException e) {
      throw e;
    } catch (Exception e) {
      throw new RangeParseException(
          "Failed to parse range value: " + e.getMessage(), rangeString, 0, e);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T extends Comparable<?>> Range<T> buildRange(
      String lowerPart,
      String upperPart,
      BoundType lowerBoundType,
      BoundType upperBoundType,
      boolean lowerUnbounded,
      boolean upperUnbounded,
      TypeAdapter<T> adapter) {

    // Case: (-∞..+∞) = all
    if (lowerUnbounded && upperUnbounded) {
      return Range.all();
    }

    // Case: (-∞..b] or (-∞..b)
    if (lowerUnbounded) {
      T upper = adapter.parse(upperPart);
      return upperBoundType == BoundType.CLOSED ? Range.atMost(upper) : Range.lessThan(upper);
    }

    // Case: [a..+∞) or (a..+∞)
    if (upperUnbounded) {
      T lower = adapter.parse(lowerPart);
      return lowerBoundType == BoundType.CLOSED ? Range.atLeast(lower) : Range.greaterThan(lower);
    }

    // Case: bounded range
    T lower = adapter.parse(lowerPart);
    T upper = adapter.parse(upperPart);

    if (lowerBoundType == BoundType.CLOSED && upperBoundType == BoundType.CLOSED) {
      return Range.closed(lower, upper);
    } else if (lowerBoundType == BoundType.CLOSED && upperBoundType == BoundType.OPEN) {
      return Range.closedOpen(lower, upper);
    } else if (lowerBoundType == BoundType.OPEN && upperBoundType == BoundType.CLOSED) {
      return Range.openClosed(lower, upper);
    } else {
      return Range.open(lower, upper);
    }
  }

  /**
   * Builder for creating configured {@link RangeParser} instances.
   */
  public static final class Builder {
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();
    private boolean lenient = false;

    private Builder() {}

    /**
     * Registers a custom type adapter for parsing range elements.
     *
     * @param type the class of elements to parse
     * @param adapter the adapter to use for parsing
     * @param <T> the element type
     * @return this builder
     */
    public <T extends Comparable<T>> Builder registerType(Class<T> type, TypeAdapter<T> adapter) {
      typeAdapters.put(type, adapter);
      return this;
    }

    /**
     * Enables lenient parsing mode.
     *
     * <p>In lenient mode:
     * <ul>
     *   <li>Bracket-less notation like "0..100" is accepted (treated as [0..100))
     *   <li>Various infinity representations are accepted
     * </ul>
     *
     * @param lenient true to enable lenient mode
     * @return this builder
     */
    public Builder lenient(boolean lenient) {
      this.lenient = lenient;
      return this;
    }

    /**
     * Builds the configured RangeParser.
     *
     * @return a new RangeParser instance
     */
    public RangeParser build() {
      return new RangeParser(this);
    }
  }
}