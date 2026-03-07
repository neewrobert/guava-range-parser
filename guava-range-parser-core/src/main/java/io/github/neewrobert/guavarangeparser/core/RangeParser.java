package io.github.neewrobert.guavarangeparser.core;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
 * <p><b>Thread Safety:</b> Instances of this class are immutable and thread-safe. A single parser
 * instance can be safely shared across multiple threads.
 *
 * @see Range
 * @see RangeFormatter
 */
public final class RangeParser {

  private static final String SEPARATOR = "..";

  private static final Set<String> POSITIVE_INFINITY =
      Set.of("+∞", "∞", "+inf", "inf", "+INF", "INF", "+Infinity", "Infinity");

  private static final Set<String> NEGATIVE_INFINITY = Set.of("-∞", "-inf", "-INF", "-Infinity");

  /**
   * Internal record to hold parsed range parts.
   *
   * @param lowerPart the lower bound string value
   * @param upperPart the upper bound string value
   * @param lowerBoundType the type of the lower bound (OPEN or CLOSED)
   * @param upperBoundType the type of the upper bound (OPEN or CLOSED)
   * @param lowerUnbounded whether the lower bound is infinity
   * @param upperUnbounded whether the upper bound is infinity
   * @param openBracketPos position of opening bracket in original input, or -1
   * @param closeBracketPos position of closing bracket in original input, or -1
   * @param lowerPartPos position of lower bound value in original input
   * @param upperPartPos position of upper bound value in original input
   */
  private record RangeParts(
      String lowerPart,
      String upperPart,
      BoundType lowerBoundType,
      BoundType upperBoundType,
      boolean lowerUnbounded,
      boolean upperUnbounded,
      int openBracketPos,
      int closeBracketPos,
      int lowerPartPos,
      int upperPartPos) {}

  /**
   * Maximum allowed length for input strings.
   *
   * <p>This limit prevents denial-of-service attacks via extremely long input strings that could
   * cause memory exhaustion or excessive regex processing time.
   */
  private static final int MAX_INPUT_LENGTH = 1000;

  private static final String INVALID_FORMAT_MESSAGE =
      "Invalid range format. Expected notation like '[a..b)', '(a..b]', '(-∞..+∞)', etc.";

  private static final RangeParser DEFAULT_INSTANCE = builder().build();

  private final Map<Class<?>, TypeAdapter<?>> typeAdapters;
  private final boolean lenient;

  private RangeParser(Builder builder) {
    // Register built-in adapters first, then overlay custom adapters
    // This allows custom adapters to override built-in ones
    Map<Class<?>, TypeAdapter<?>> adapters = new HashMap<>();
    BuiltInTypeAdapters.registerAll(adapters);
    adapters.putAll(builder.typeAdapters);
    this.typeAdapters = Map.copyOf(adapters);
    this.lenient = builder.lenient;
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
    return DEFAULT_INSTANCE.parseRange(rangeString, elementType);
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
  public <T extends Comparable<?>> Range<T> parseRange(String rangeString, Class<T> elementType) {
    validateInput(rangeString, elementType);
    String normalized = normalizeInput(rangeString);
    RangeParts parts = extractRangeParts(normalized, rangeString);
    validateInfinityBounds(parts, rangeString);
    TypeAdapter<T> adapter = getTypeAdapter(elementType, rangeString);

    try {
      return buildRange(parts, adapter, rangeString);
    } catch (Exception e) {
      if (e instanceof RangeParseException rpe) {
        throw rpe;
      }
      throw new RangeParseException(
          "Failed to parse range value: " + e.getMessage(), rangeString, -1, e);
    }
  }

  /** Validates that input parameters are not null and input string is within size limits. */
  private void validateInput(String rangeString, Class<?> elementType) {
    requireNonNull(rangeString, "rangeString must not be null");
    requireNonNull(elementType, "elementType must not be null");

    if (rangeString.length() > MAX_INPUT_LENGTH) {
      throw new RangeParseException(
          "Input exceeds maximum length of " + MAX_INPUT_LENGTH + " characters",
          rangeString.substring(0, 50) + "...",
          -1);
    }
  }

  /** Normalizes input by trimming whitespace and applying lenient mode if enabled. */
  private String normalizeInput(String rangeString) {
    String trimmed = rangeString.trim();
    if (trimmed.isEmpty()) {
      throw new RangeParseException("Range string cannot be empty", rangeString, -1);
    }

    if (lenient) {
      boolean hasOpeningBracket = trimmed.startsWith("[") || trimmed.startsWith("(");
      boolean hasClosingBracket = trimmed.endsWith("]") || trimmed.endsWith(")");

      if (!hasOpeningBracket && !hasClosingBracket) {
        return "[" + trimmed + ")";
      }
    }

    return trimmed;
  }

  /**
   * Extracts and validates range parts from the normalized input string.
   *
   * @param normalized the normalized input string
   * @param original the original input string (for error messages)
   * @return parsed range parts
   */
  private RangeParts extractRangeParts(String normalized, String original) {
    int trimOffset = original.length() - original.stripLeading().length();
    boolean lenientWrapped = normalized.length() != original.trim().length();
    int bracketOffset = lenientWrapped ? 0 : 1;

    validateFormat(normalized, original, trimOffset);

    char openingBracket = normalized.charAt(0);
    char closingBracket = normalized.charAt(normalized.length() - 1);

    String content = normalized.substring(1, normalized.length() - 1);
    int separatorIndex = content.indexOf(SEPARATOR);
    if (separatorIndex == -1) {
      throw new RangeParseException(INVALID_FORMAT_MESSAGE, original, trimOffset + bracketOffset);
    }

    String lowerRaw = content.substring(0, separatorIndex);
    String upperRaw = content.substring(separatorIndex + SEPARATOR.length());
    String lowerPart = lowerRaw.trim();
    String upperPart = upperRaw.trim();

    if (lowerPart.isEmpty()) {
      throw new RangeParseException(INVALID_FORMAT_MESSAGE, original, trimOffset + bracketOffset);
    }
    if (upperPart.isEmpty()) {
      throw new RangeParseException(
          INVALID_FORMAT_MESSAGE,
          original,
          trimOffset + bracketOffset + separatorIndex + SEPARATOR.length());
    }

    BoundType lowerBoundType = openingBracket == '[' ? BoundType.CLOSED : BoundType.OPEN;
    BoundType upperBoundType = closingBracket == ']' ? BoundType.CLOSED : BoundType.OPEN;

    boolean lowerUnbounded = NEGATIVE_INFINITY.contains(lowerPart);
    boolean upperUnbounded = POSITIVE_INFINITY.contains(upperPart);

    // Compute positions of trimmed values within the original string
    int lowerLeadingSpaces = lowerRaw.length() - lowerRaw.stripLeading().length();
    int upperLeadingSpaces = upperRaw.length() - upperRaw.stripLeading().length();
    int openBracketPos = lenientWrapped ? -1 : trimOffset;
    int closeBracketPos = lenientWrapped ? -1 : trimOffset + normalized.length() - 1;
    int lowerPartPos = trimOffset + bracketOffset + lowerLeadingSpaces;
    int upperPartPos =
        trimOffset + bracketOffset + separatorIndex + SEPARATOR.length() + upperLeadingSpaces;

    return new RangeParts(
        lowerPart,
        upperPart,
        lowerBoundType,
        upperBoundType,
        lowerUnbounded,
        upperUnbounded,
        openBracketPos,
        closeBracketPos,
        lowerPartPos,
        upperPartPos);
  }

  /** Validates the basic format of the range string (brackets and length). */
  private void validateFormat(String normalized, String original, int trimOffset) {
    if (normalized.length() < 6) { // Minimum: "[a..b]"
      throw new RangeParseException(INVALID_FORMAT_MESSAGE, original, -1);
    }

    char openingBracket = normalized.charAt(0);
    char closingBracket = normalized.charAt(normalized.length() - 1);

    boolean badOpening = openingBracket != '[' && openingBracket != '(';
    boolean badClosing = closingBracket != ']' && closingBracket != ')';

    if (badOpening || badClosing) {
      int pos = badOpening ? trimOffset : trimOffset + normalized.length() - 1;
      throw new RangeParseException(INVALID_FORMAT_MESSAGE, original, pos);
    }
  }

  /** Validates that infinity bounds are open (mathematical convention). */
  private void validateInfinityBounds(RangeParts parts, String rangeString) {
    if (parts.lowerUnbounded() && parts.lowerBoundType() == BoundType.CLOSED) {
      throw new RangeParseException(
          "Invalid range: negative infinity bound must be open '(' not closed '['",
          rangeString,
          parts.openBracketPos());
    }
    if (parts.upperUnbounded() && parts.upperBoundType() == BoundType.CLOSED) {
      throw new RangeParseException(
          "Invalid range: positive infinity bound must be open ')' not closed ']'",
          rangeString,
          parts.closeBracketPos());
    }
  }

  /** Gets the type adapter for the given element type. */
  @SuppressWarnings("unchecked")
  private <T extends Comparable<?>> TypeAdapter<T> getTypeAdapter(
      Class<T> elementType, String rangeString) {
    TypeAdapter<T> adapter = (TypeAdapter<T>) typeAdapters.get(elementType);
    if (adapter == null) {
      throw new RangeParseException(
          "No type adapter registered for: " + elementType.getName(), rangeString, -1);
    }
    return adapter;
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<?>> Range<T> buildRange(
      RangeParts parts, TypeAdapter<T> adapter, String originalInput) {

    if (parts.lowerUnbounded() && parts.upperUnbounded()) {
      return Range.all();
    }

    if (parts.lowerUnbounded()) {
      T upper =
          parseAndValidate(
              adapter, parts.upperPart(), "upper", originalInput, parts.upperPartPos());
      return parts.upperBoundType() == BoundType.CLOSED
          ? Range.atMost(upper)
          : Range.lessThan(upper);
    }

    if (parts.upperUnbounded()) {
      T lower =
          parseAndValidate(
              adapter, parts.lowerPart(), "lower", originalInput, parts.lowerPartPos());
      return parts.lowerBoundType() == BoundType.CLOSED
          ? Range.atLeast(lower)
          : Range.greaterThan(lower);
    }

    T lower =
        parseAndValidate(adapter, parts.lowerPart(), "lower", originalInput, parts.lowerPartPos());
    T upper =
        parseAndValidate(adapter, parts.upperPart(), "upper", originalInput, parts.upperPartPos());

    // Validate lower <= upper (compare using Comparable)
    @SuppressWarnings("unchecked")
    Comparable<Object> comparableLower = (Comparable<Object>) lower;
    if (comparableLower.compareTo(upper) > 0) {
      throw new RangeParseException(
          "Invalid range: lower bound ("
              + parts.lowerPart()
              + ") is greater than upper bound ("
              + parts.upperPart()
              + ")",
          originalInput,
          parts.lowerPartPos());
    }

    return switch (parts.lowerBoundType()) {
      case CLOSED ->
          switch (parts.upperBoundType()) {
            case CLOSED -> Range.closed(lower, upper);
            case OPEN -> Range.closedOpen(lower, upper);
          };
      case OPEN ->
          switch (parts.upperBoundType()) {
            case CLOSED -> Range.openClosed(lower, upper);
            case OPEN -> Range.open(lower, upper);
          };
    };
  }

  /** Parses a value using the adapter and validates the result is not null. */
  private <T> T parseAndValidate(
      TypeAdapter<T> adapter, String value, String boundName, String originalInput, int valuePos) {
    T result = adapter.parse(value);
    if (result == null) {
      throw new RangeParseException(
          "TypeAdapter returned null for " + boundName + " bound value: " + value,
          originalInput,
          valuePos);
    }
    return result;
  }

  /**
   * Builder for creating configured {@link RangeParser} instances.
   *
   * <p>The builder can be reused to create multiple parser instances. Each call to {@link #build()}
   * creates an independent parser with its own copy of the type adapters.
   *
   * <p>Custom type adapters registered via {@link #registerType} take precedence over built-in
   * adapters, allowing you to override the default parsing behavior for any type.
   *
   * <p><b>Thread Safety:</b> This builder is not thread-safe. Do not share builder instances across
   * threads without external synchronization. However, the {@link RangeParser} instances created by
   * this builder are immutable and thread-safe.
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
      requireNonNull(type, "type must not be null");
      requireNonNull(adapter, "adapter must not be null");
      typeAdapters.put(type, adapter);
      return this;
    }

    /**
     * Enables lenient parsing mode.
     *
     * <p>In lenient mode:
     *
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
