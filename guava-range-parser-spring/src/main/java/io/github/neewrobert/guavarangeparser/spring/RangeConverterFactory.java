package io.github.neewrobert.guavarangeparser.spring;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeParseException;
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

/**
 * Spring converter for converting String to Guava {@link Range} objects.
 *
 * <p>This converter uses standard Guava Range notation (e.g., "[0..100)", "(-∞..+∞)") and supports
 * all built-in types provided by {@link RangeParser}.
 *
 * <p>Example configuration usage:
 *
 * <pre>{@code
 * @ConfigurationProperties(prefix = "my-app")
 * public class MyProperties {
 *     private Range<Integer> stockRange;      // [0..100)
 *     private Range<Duration> refreshInterval; // [PT1M..PT5M]
 * }
 * }</pre>
 *
 * <pre>
 * my-app.stock-range=[0..100)
 * my-app.refresh-interval=[PT1M..PT5M]
 * </pre>
 *
 * @see RangeParser
 * @see RangeConverterAutoConfiguration
 */
public class RangeConverterFactory implements GenericConverter {

  private final RangeParser parser;

  /** Creates a new converter with default parser settings. */
  public RangeConverterFactory() {
    this(RangeParser.builder().build());
  }

  /**
   * Creates a new converter with a custom parser.
   *
   * @param parser the parser to use for conversion
   * @throws NullPointerException if parser is null
   */
  public RangeConverterFactory(RangeParser parser) {
    this.parser = requireNonNull(parser, "parser must not be null");
  }

  @Override
  public ImmutableSet<ConvertiblePair> getConvertibleTypes() {
    return ImmutableSet.of(new ConvertiblePair(String.class, Range.class));
  }

  @Override
  public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (source == null) {
      return null;
    }

    requireNonNull(targetType, "targetType must not be null");

    String rangeString = source.toString();
    if (rangeString.isBlank()) {
      return null;
    }

    Class<?> elementType = resolveElementType(targetType);

    try {
      return parseRange(rangeString, elementType);
    } catch (RangeParseException e) {
      throw new IllegalArgumentException(
          "Failed to convert '" + rangeString + "' to Range<" + elementType.getSimpleName() + ">",
          e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<?>> Range<T> parseRange(String rangeString, Class<?> elementType) {
    return parser.parseRange(rangeString, (Class<T>) elementType);
  }

  private static Class<?> resolveElementType(TypeDescriptor targetType) {
    ResolvableType resolvableType = targetType.getResolvableType();
    ResolvableType generic = resolvableType.getGeneric(0);
    Class<?> resolved = generic.resolve();
    // Check if type could not be resolved, or resolved to just Comparable (the type bound)
    // which indicates a raw Range type without explicit generic parameter
    if (resolved == null || resolved == Comparable.class) {
      throw new IllegalArgumentException(
          "Cannot determine Range element type. "
              + "Use a parameterized type like Range<Integer> instead of raw Range. "
              + "Target type: "
              + targetType);
    }
    return resolved;
  }
}
