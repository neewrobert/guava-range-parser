package io.github.guavarangeparser.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Built-in type adapters for common Comparable types.
 *
 * <p>This class provides pre-configured {@link TypeAdapter} implementations for:
 *
 * <ul>
 *   <li>Numeric types: Integer, Long, Short, Byte, Double, Float, BigInteger, BigDecimal
 *   <li>Temporal types: Duration, Instant, LocalDate, LocalDateTime, LocalTime, ZonedDateTime,
 *       OffsetDateTime
 *   <li>Other types: String, Character
 * </ul>
 *
 * @see TypeAdapter
 * @see RangeParser
 */
public final class BuiltInTypeAdapters {

  private BuiltInTypeAdapters() {
    // Utility class
  }

  // Numeric adapters
  public static final TypeAdapter<Integer> INTEGER = Integer::valueOf;
  public static final TypeAdapter<Long> LONG = Long::valueOf;
  public static final TypeAdapter<Short> SHORT = Short::valueOf;
  public static final TypeAdapter<Byte> BYTE = Byte::valueOf;
  public static final TypeAdapter<Double> DOUBLE = Double::valueOf;
  public static final TypeAdapter<Float> FLOAT = Float::valueOf;
  public static final TypeAdapter<BigInteger> BIG_INTEGER = BigInteger::new;
  public static final TypeAdapter<BigDecimal> BIG_DECIMAL = BigDecimal::new;

  // Temporal adapters
  public static final TypeAdapter<Duration> DURATION = Duration::parse;
  public static final TypeAdapter<Instant> INSTANT = Instant::parse;
  public static final TypeAdapter<LocalDate> LOCAL_DATE = LocalDate::parse;
  public static final TypeAdapter<LocalDateTime> LOCAL_DATE_TIME = LocalDateTime::parse;
  public static final TypeAdapter<LocalTime> LOCAL_TIME = LocalTime::parse;
  public static final TypeAdapter<ZonedDateTime> ZONED_DATE_TIME = ZonedDateTime::parse;
  public static final TypeAdapter<OffsetDateTime> OFFSET_DATE_TIME = OffsetDateTime::parse;

  // Other adapters
  public static final TypeAdapter<String> STRING = s -> s;
  public static final TypeAdapter<Character> CHARACTER =
      s -> {
        if (s.length() != 1) {
          throw new IllegalArgumentException("Expected single character but got: '" + s + "'");
        }
        return s.charAt(0);
      };

  /**
   * Registers all built-in type adapters into the provided map.
   *
   * @param adapters the map to register adapters into
   */
  static void registerAll(Map<Class<?>, TypeAdapter<?>> adapters) {
    // Numeric types (including primitives)
    adapters.put(Integer.class, INTEGER);
    adapters.put(int.class, INTEGER);
    adapters.put(Long.class, LONG);
    adapters.put(long.class, LONG);
    adapters.put(Short.class, SHORT);
    adapters.put(short.class, SHORT);
    adapters.put(Byte.class, BYTE);
    adapters.put(byte.class, BYTE);
    adapters.put(Double.class, DOUBLE);
    adapters.put(double.class, DOUBLE);
    adapters.put(Float.class, FLOAT);
    adapters.put(float.class, FLOAT);
    adapters.put(BigInteger.class, BIG_INTEGER);
    adapters.put(BigDecimal.class, BIG_DECIMAL);

    // Temporal types
    adapters.put(Duration.class, DURATION);
    adapters.put(Instant.class, INSTANT);
    adapters.put(LocalDate.class, LOCAL_DATE);
    adapters.put(LocalDateTime.class, LOCAL_DATE_TIME);
    adapters.put(LocalTime.class, LOCAL_TIME);
    adapters.put(ZonedDateTime.class, ZONED_DATE_TIME);
    adapters.put(OffsetDateTime.class, OFFSET_DATE_TIME);

    // Other types
    adapters.put(String.class, STRING);
    adapters.put(Character.class, CHARACTER);
    adapters.put(char.class, CHARACTER);
  }
}
