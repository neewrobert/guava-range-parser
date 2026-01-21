package io.github.neewrobert.guavarangeparser.core;

/**
 * Adapter interface for parsing string values into Comparable types.
 *
 * <p>Implementations of this interface are used by {@link RangeParser} to convert the string
 * representation of range endpoints into actual values.
 *
 * <p>Example implementation for a custom Money type:
 *
 * <pre>{@code
 * TypeAdapter<Money> moneyAdapter = Money::parse;
 * }</pre>
 *
 * <p>Note: The type parameter does not enforce {@code Comparable<T>} because some types like {@code
 * LocalDate} implement {@code Comparable<ChronoLocalDate>} rather than {@code
 * Comparable<LocalDate>}. The Guava Range class handles the Comparable constraint at runtime.
 *
 * @param <T> the type to parse
 * @see RangeParser
 * @see BuiltInTypeAdapters
 */
@FunctionalInterface
public interface TypeAdapter<T> {

  /**
   * Parses a string value into the target type.
   *
   * @param value the string value to parse
   * @return the parsed value
   * @throws IllegalArgumentException if the value cannot be parsed
   * @throws NumberFormatException if the value is expected to be numeric but isn't
   */
  T parse(String value);
}
