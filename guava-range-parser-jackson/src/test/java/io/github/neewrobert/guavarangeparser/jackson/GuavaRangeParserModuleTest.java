package io.github.neewrobert.guavarangeparser.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.InfinityStyle;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GuavaRangeParserModuleTest {

  @Nested
  class ModuleConfiguration {

    @Test
    void defaultModuleHasSymbolInfinityStyle() {
      GuavaRangeParserModule module = new GuavaRangeParserModule();
      assertThat(module.getInfinityStyle()).isEqualTo(InfinityStyle.SYMBOL);
    }

    @Test
    void builderConfiguresInfinityStyle() {
      GuavaRangeParserModule module =
          GuavaRangeParserModule.builder().infinityStyle(InfinityStyle.WORD_FULL).build();
      assertThat(module.getInfinityStyle()).isEqualTo(InfinityStyle.WORD_FULL);
    }

    @Test
    void moduleHasCorrectName() {
      GuavaRangeParserModule module = new GuavaRangeParserModule();
      assertThat(module.getModuleName()).isEqualTo("guava-range-parser");
    }

    @Test
    void moduleHasVersion() {
      GuavaRangeParserModule module = new GuavaRangeParserModule();
      assertThat(module.version()).isNotNull();
    }
  }

  @Nested
  class StringNotationSerialization {

    private final ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaRangeParserModule());

    @Test
    void serializeClosedRange() throws JsonProcessingException {
      Range<Integer> range = Range.closed(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0..100]\"");
    }

    @Test
    void serializeOpenRange() throws JsonProcessingException {
      Range<Integer> range = Range.open(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"(0..100)\"");
    }

    @Test
    void serializeClosedOpenRange() throws JsonProcessingException {
      Range<Integer> range = Range.closedOpen(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0..100)\"");
    }

    @Test
    void serializeOpenClosedRange() throws JsonProcessingException {
      Range<Integer> range = Range.openClosed(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"(0..100]\"");
    }

    @Test
    void serializeAtLeastRange() throws JsonProcessingException {
      Range<Integer> range = Range.atLeast(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0..+∞)\"");
    }

    @Test
    void serializeGreaterThanRange() throws JsonProcessingException {
      Range<Integer> range = Range.greaterThan(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"(0..+∞)\"");
    }

    @Test
    void serializeAtMostRange() throws JsonProcessingException {
      Range<Integer> range = Range.atMost(100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"(-∞..100]\"");
    }

    @Test
    void serializeLessThanRange() throws JsonProcessingException {
      Range<Integer> range = Range.lessThan(100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"(-∞..100)\"");
    }

    @Test
    void serializeAllRange() throws JsonProcessingException {
      Range<Integer> range = Range.all();
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"(-∞..+∞)\"");
    }

    @Test
    void serializeDoubleRange() throws JsonProcessingException {
      Range<Double> range = Range.closedOpen(0.5, 1.5);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0.5..1.5)\"");
    }

    @Test
    void serializeStringRange() throws JsonProcessingException {
      Range<String> range = Range.closed("a", "z");
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[a..z]\"");
    }
  }

  @Nested
  class InfinityStyleSerialization {

    @Test
    void serializeWithSymbolStyle() throws JsonProcessingException {
      ObjectMapper mapper =
          new ObjectMapper()
              .registerModule(
                  GuavaRangeParserModule.builder().infinityStyle(InfinityStyle.SYMBOL).build());

      Range<Integer> range = Range.atLeast(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0..+∞)\"");
    }

    @Test
    void serializeWithWordFullStyle() throws JsonProcessingException {
      ObjectMapper mapper =
          new ObjectMapper()
              .registerModule(
                  GuavaRangeParserModule.builder().infinityStyle(InfinityStyle.WORD_FULL).build());

      Range<Integer> range = Range.atLeast(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0..+Infinity)\"");
    }

    @Test
    void serializeWithWordLowerStyle() throws JsonProcessingException {
      ObjectMapper mapper =
          new ObjectMapper()
              .registerModule(
                  GuavaRangeParserModule.builder().infinityStyle(InfinityStyle.WORD_LOWER).build());

      Range<Integer> range = Range.atLeast(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0..+inf)\"");
    }

    @Test
    void serializeWithWordUpperStyle() throws JsonProcessingException {
      ObjectMapper mapper =
          new ObjectMapper()
              .registerModule(
                  GuavaRangeParserModule.builder().infinityStyle(InfinityStyle.WORD_UPPER).build());

      Range<Integer> range = Range.atLeast(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[0..+INF)\"");
    }
  }

  @Nested
  class StringNotationDeserialization {

    private final ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaRangeParserModule());

    @Test
    void deserializeClosedRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"[0..100]\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.closed(0, 100));
    }

    @Test
    void deserializeOpenRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"(0..100)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.open(0, 100));
    }

    @Test
    void deserializeClosedOpenRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"[0..100)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void deserializeOpenClosedRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"(0..100]\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.openClosed(0, 100));
    }

    @Test
    void deserializeAtLeastRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"[0..+∞)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @Test
    void deserializeGreaterThanRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"(0..+∞)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.greaterThan(0));
    }

    @Test
    void deserializeAtMostRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"(-∞..100]\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.atMost(100));
    }

    @Test
    void deserializeLessThanRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"(-∞..100)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.lessThan(100));
    }

    @Test
    void deserializeAllRange() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"(-∞..+∞)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.all());
    }

    @Test
    void deserializeWithInfVariant() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"[0..+inf)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @Test
    void deserializeWithInfinityVariant() throws JsonProcessingException {
      Range<Integer> range = mapper.readValue("\"[0..+Infinity)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @Test
    void deserializeDoubleRange() throws JsonProcessingException {
      Range<Double> range = mapper.readValue("\"[0.5..1.5)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.closedOpen(0.5, 1.5));
    }

    @Test
    void deserializeLongRange() throws JsonProcessingException {
      Range<Long> range = mapper.readValue("\"[0..9999999999]\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.closed(0L, 9999999999L));
    }

    @Test
    void deserializeStringRange() throws JsonProcessingException {
      Range<String> range = mapper.readValue("\"[a..z]\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.closed("a", "z"));
    }

    @Test
    void deserializeLocalDateRange() throws JsonProcessingException {
      Range<LocalDate> range =
          mapper.readValue("\"[2024-01-01..2024-12-31]\"", new TypeReference<>() {});
      assertThat(range)
          .isEqualTo(Range.closed(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }

    @Test
    void deserializeDurationRange() throws JsonProcessingException {
      Range<Duration> range = mapper.readValue("\"[PT1H..PT24H)\"", new TypeReference<>() {});
      assertThat(range).isEqualTo(Range.closedOpen(Duration.ofHours(1), Duration.ofHours(24)));
    }
  }

  @Nested
  class RoundTrip {

    private final ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaRangeParserModule());

    @Test
    void roundTripClosedRange() throws JsonProcessingException {
      Range<Integer> original = Range.closed(0, 100);
      String json = mapper.writeValueAsString(original);
      Range<Integer> deserialized = mapper.readValue(json, new TypeReference<>() {});
      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void roundTripAllRange() throws JsonProcessingException {
      Range<Integer> original = Range.all();
      String json = mapper.writeValueAsString(original);
      Range<Integer> deserialized = mapper.readValue(json, new TypeReference<>() {});
      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void roundTripAtLeastRange() throws JsonProcessingException {
      Range<Integer> original = Range.atLeast(50);
      String json = mapper.writeValueAsString(original);
      Range<Integer> deserialized = mapper.readValue(json, new TypeReference<>() {});
      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void roundTripDoubleRange() throws JsonProcessingException {
      Range<Double> original = Range.closedOpen(0.1, 0.9);
      String json = mapper.writeValueAsString(original);
      Range<Double> deserialized = mapper.readValue(json, new TypeReference<>() {});
      assertThat(deserialized).isEqualTo(original);
    }
  }

  @Nested
  class ErrorHandling {

    private final ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaRangeParserModule());

    @Test
    void throwsOnInvalidFormat() {
      assertThatThrownBy(
              () -> mapper.readValue("\"invalid\"", new TypeReference<Range<Integer>>() {}))
          .isInstanceOf(InvalidFormatException.class);
    }

    @Test
    void throwsOnInvalidNumber() {
      assertThatThrownBy(
              () -> mapper.readValue("\"[abc..100)\"", new TypeReference<Range<Integer>>() {}))
          .isInstanceOf(InvalidFormatException.class);
    }

    @Test
    void throwsOnJsonObjectFormat() {
      // JSON object format is not supported - use jackson-datatype-guava for that
      String json =
          """
          {
            "lowerEndpoint": 0,
            "upperEndpoint": 100,
            "lowerBoundType": "CLOSED",
            "upperBoundType": "OPEN"
          }
          """;
      assertThatThrownBy(() -> mapper.readValue(json, new TypeReference<Range<Integer>>() {}))
          .isInstanceOf(MismatchedInputException.class);
    }
  }

  @Nested
  class PojoSerialization {

    private final ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaRangeParserModule());

    @Test
    void serializePojoWithRange() throws JsonProcessingException {
      PriceFilter filter = new PriceFilter(Range.closedOpen(10, 100));
      String json = mapper.writeValueAsString(filter);
      assertThat(json).contains("\"priceRange\":\"[10..100)\"");
    }

    @Test
    void deserializePojoWithRange() throws JsonProcessingException {
      String json = "{\"priceRange\":\"[10..100)\"}";
      PriceFilter filter = mapper.readValue(json, PriceFilter.class);
      assertThat(filter.priceRange()).isEqualTo(Range.closedOpen(10, 100));
    }

    record PriceFilter(Range<Integer> priceRange) {}
  }

  @Nested
  class EdgeCases {

    private final ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaRangeParserModule());

    @Test
    @SuppressWarnings("rawtypes")
    void serializeRawRangeType() throws JsonProcessingException {
      // Test serialization with raw Range type
      Range range = Range.closed(1, 10);
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("\"[1..10]\"");
    }

    @Test
    void deserializeRawRangeTypeThrowsError() {
      // Raw Range<?> without a type parameter defaults to Object which has no adapter
      assertThatThrownBy(() -> mapper.readValue("\"[0..100]\"", Range.class))
          .isInstanceOf(InvalidFormatException.class)
          .hasMessageContaining("No type adapter registered for: java.lang.Object");
    }

    @Test
    void deserializeNullValue() throws JsonProcessingException {
      // Test null handling
      Range<Integer> result = mapper.readValue("null", new TypeReference<>() {});
      assertThat(result).isNull();
    }

    @Test
    void deserializeNumberTokenThrowsError() {
      // Test when JSON contains a number instead of string
      assertThatThrownBy(() -> mapper.readValue("123", new TypeReference<Range<Integer>>() {}))
          .isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void deserializeBooleanTokenThrowsError() {
      // Test when JSON contains a boolean instead of string
      assertThatThrownBy(() -> mapper.readValue("true", new TypeReference<Range<Integer>>() {}))
          .isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void deserializeArrayTokenThrowsError() {
      // Test when JSON contains an array instead of string
      assertThatThrownBy(
              () -> mapper.readValue("[1, 2, 3]", new TypeReference<Range<Integer>>() {}))
          .isInstanceOf(MismatchedInputException.class);
    }

    @Test
    void deserializeInvalidRangeNotationInPojo() {
      // Test that invalid range notation in POJO triggers error handling (line 61 coverage)
      String json = "{\"testRange\":\"not-a-valid-range\"}";
      assertThatThrownBy(() -> mapper.readValue(json, TestRecord.class))
          .isInstanceOf(InvalidFormatException.class)
          .hasMessageContaining("Invalid range format");
    }

    record TestRecord(Range<Integer> testRange) {}
  }
}
