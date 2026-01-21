package io.github.neewrobert.guavarangeparser.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

class RangeConverterFactoryTest {

  private final RangeConverterFactory converter = new RangeConverterFactory();

  @Nested
  class ConvertibleTypes {

    @Test
    void supportsStringToRangeConversion() {
      var pairs = converter.getConvertibleTypes();
      assertThat(pairs).hasSize(1);
      var pair = pairs.iterator().next();
      assertThat(pair.getSourceType()).isEqualTo(String.class);
      assertThat(pair.getTargetType()).isEqualTo(Range.class);
    }
  }

  @Nested
  class IntegerRangeConversion {

    @Test
    void convertClosedRange() {
      Range<?> result = convert("[0..100]", Integer.class);
      assertThat(result).isEqualTo(Range.closed(0, 100));
    }

    @Test
    void convertOpenRange() {
      Range<?> result = convert("(0..100)", Integer.class);
      assertThat(result).isEqualTo(Range.open(0, 100));
    }

    @Test
    void convertClosedOpenRange() {
      Range<?> result = convert("[0..100)", Integer.class);
      assertThat(result).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void convertOpenClosedRange() {
      Range<?> result = convert("(0..100]", Integer.class);
      assertThat(result).isEqualTo(Range.openClosed(0, 100));
    }

    @Test
    void convertAtLeastRange() {
      Range<?> result = convert("[0..+∞)", Integer.class);
      assertThat(result).isEqualTo(Range.atLeast(0));
    }

    @Test
    void convertAtMostRange() {
      Range<?> result = convert("(-∞..100]", Integer.class);
      assertThat(result).isEqualTo(Range.atMost(100));
    }

    @Test
    void convertAllRange() {
      Range<?> result = convert("(-∞..+∞)", Integer.class);
      assertThat(result).isEqualTo(Range.all());
    }

    @Test
    void convertNegativeNumbers() {
      Range<?> result = convert("[-100..-10]", Integer.class);
      assertThat(result).isEqualTo(Range.closed(-100, -10));
    }
  }

  @Nested
  class OtherTypeConversion {

    @Test
    void convertLongRange() {
      Range<?> result = convert("[0..9999999999]", Long.class);
      assertThat(result).isEqualTo(Range.closed(0L, 9999999999L));
    }

    @Test
    void convertDoubleRange() {
      Range<?> result = convert("[0.5..1.5)", Double.class);
      assertThat(result).isEqualTo(Range.closedOpen(0.5, 1.5));
    }

    @Test
    void convertStringRange() {
      Range<?> result = convert("[a..z]", String.class);
      assertThat(result).isEqualTo(Range.closed("a", "z"));
    }

    @Test
    void convertLocalDateRange() {
      Range<?> result = convert("[2024-01-01..2024-12-31]", LocalDate.class);
      assertThat(result)
          .isEqualTo(Range.closed(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }

    @Test
    void convertDurationRange() {
      Range<?> result = convert("[PT1H..PT24H)", Duration.class);
      assertThat(result).isEqualTo(Range.closedOpen(Duration.ofHours(1), Duration.ofHours(24)));
    }
  }

  @Nested
  class NullAndBlankHandling {

    @Test
    void returnsNullForNullSource() {
      TypeDescriptor targetType = createTypeDescriptor(Integer.class);
      Object result = converter.convert(null, TypeDescriptor.valueOf(String.class), targetType);
      assertThat(result).isNull();
    }

    @Test
    void returnsNullForBlankString() {
      Range<?> result = convert("   ", Integer.class);
      assertThat(result).isNull();
    }

    @Test
    void returnsNullForEmptyString() {
      Range<?> result = convert("", Integer.class);
      assertThat(result).isNull();
    }
  }

  @Nested
  class ErrorHandling {

    @Test
    void throwsOnInvalidFormat() {
      assertThatThrownBy(() -> convert("invalid", Integer.class))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Failed to convert")
          .hasMessageContaining("Range<Integer>");
    }

    @Test
    void throwsOnInvalidNumber() {
      assertThatThrownBy(() -> convert("[abc..100)", Integer.class))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Failed to convert");
    }

    @Test
    void throwsOnMissingBrackets() {
      assertThatThrownBy(() -> convert("0..100", Integer.class))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsOnNullParser() {
      assertThatThrownBy(() -> new RangeConverterFactory(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("parser must not be null");
    }

    @Test
    void throwsOnNullTargetType() {
      assertThatThrownBy(
              () -> converter.convert("[0..100]", TypeDescriptor.valueOf(String.class), null))
          .isInstanceOf(NullPointerException.class)
          .hasMessageContaining("targetType must not be null");
    }
  }

  @Nested
  class CustomParser {

    @Test
    void usesProvidedParser() {
      RangeParser lenientParser = RangeParser.builder().lenient(true).build();
      RangeConverterFactory customConverter = new RangeConverterFactory(lenientParser);

      // Lenient parser accepts bracket-less notation
      TypeDescriptor targetType = createTypeDescriptor(Integer.class);
      Object result =
          customConverter.convert("0..100", TypeDescriptor.valueOf(String.class), targetType);

      assertThat(result).isEqualTo(Range.closedOpen(0, 100));
    }
  }

  @Nested
  class TypeResolution {

    @Test
    void resolvesTypeFromGenericParameter() {
      // Verify that type resolution works correctly with explicit generic type
      TypeDescriptor targetType = createTypeDescriptor(Long.class);
      Object result =
          converter.convert("[0..100]", TypeDescriptor.valueOf(String.class), targetType);

      // Should parse as Long, not Integer
      assertThat(result).isEqualTo(Range.closed(0L, 100L));
    }

    @Test
    void throwsWhenGenericTypeCannotBeResolved() {
      // Raw Range type without generic parameter
      TypeDescriptor rawRangeType = TypeDescriptor.valueOf(Range.class);

      assertThatThrownBy(
              () ->
                  converter.convert("[0..100]", TypeDescriptor.valueOf(String.class), rawRangeType))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Cannot determine Range element type")
          .hasMessageContaining("parameterized type");
    }
  }

  private Range<?> convert(String source, Class<?> elementType) {
    TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
    TypeDescriptor targetType = createTypeDescriptor(elementType);
    return (Range<?>) converter.convert(source, sourceType, targetType);
  }

  private static TypeDescriptor createTypeDescriptor(Class<?> elementType) {
    ResolvableType rangeType = ResolvableType.forClassWithGenerics(Range.class, elementType);
    return new TypeDescriptor(rangeType, Range.class, null);
  }
}
