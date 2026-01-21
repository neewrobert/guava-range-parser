package io.github.neewrobert.guavarangeparser.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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
    void defaultModuleHasStringNotationOutput() {
      GuavaRangeParserModule module = new GuavaRangeParserModule();
      assertThat(module.getOutputFormat()).isEqualTo(OutputFormat.STRING_NOTATION);
    }

    @Test
    void defaultModuleHasSymbolInfinityStyle() {
      GuavaRangeParserModule module = new GuavaRangeParserModule();
      assertThat(module.getInfinityStyle()).isEqualTo(InfinityStyle.SYMBOL);
    }

    @Test
    void builderConfiguresOutputFormat() {
      GuavaRangeParserModule module =
          GuavaRangeParserModule.builder().outputFormat(OutputFormat.JSON_OBJECT).build();
      assertThat(module.getOutputFormat()).isEqualTo(OutputFormat.JSON_OBJECT);
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
  class JsonObjectSerialization {

    private final ObjectMapper mapper =
        new ObjectMapper()
            .registerModule(
                GuavaRangeParserModule.builder().outputFormat(OutputFormat.JSON_OBJECT).build());

    @Test
    void serializeClosedRange() throws JsonProcessingException {
      Range<Integer> range = Range.closed(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).contains("\"lowerEndpoint\":0");
      assertThat(json).contains("\"upperEndpoint\":100");
      assertThat(json).contains("\"lowerBoundType\":\"CLOSED\"");
      assertThat(json).contains("\"upperBoundType\":\"CLOSED\"");
    }

    @Test
    void serializeAtLeastRange() throws JsonProcessingException {
      Range<Integer> range = Range.atLeast(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).contains("\"lowerEndpoint\":0");
      assertThat(json).contains("\"lowerBoundType\":\"CLOSED\"");
      assertThat(json).doesNotContain("upperEndpoint");
      assertThat(json).doesNotContain("upperBoundType");
    }

    @Test
    void serializeAtMostRange() throws JsonProcessingException {
      Range<Integer> range = Range.atMost(100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).doesNotContain("lowerEndpoint");
      assertThat(json).doesNotContain("lowerBoundType");
      assertThat(json).contains("\"upperEndpoint\":100");
      assertThat(json).contains("\"upperBoundType\":\"CLOSED\"");
    }

    @Test
    void serializeAllRange() throws JsonProcessingException {
      Range<Integer> range = Range.all();
      String json = mapper.writeValueAsString(range);
      assertThat(json).isEqualTo("{}");
    }

    @Test
    void serializeOpenRange() throws JsonProcessingException {
      Range<Integer> range = Range.open(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).contains("\"lowerBoundType\":\"OPEN\"");
      assertThat(json).contains("\"upperBoundType\":\"OPEN\"");
    }

    @Test
    void serializeOpenClosedRange() throws JsonProcessingException {
      Range<Integer> range = Range.openClosed(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).contains("\"lowerBoundType\":\"OPEN\"");
      assertThat(json).contains("\"upperBoundType\":\"CLOSED\"");
    }

    @Test
    void serializeClosedOpenRange() throws JsonProcessingException {
      Range<Integer> range = Range.closedOpen(0, 100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).contains("\"lowerBoundType\":\"CLOSED\"");
      assertThat(json).contains("\"upperBoundType\":\"OPEN\"");
    }

    @Test
    void serializeGreaterThanRange() throws JsonProcessingException {
      Range<Integer> range = Range.greaterThan(0);
      String json = mapper.writeValueAsString(range);
      assertThat(json).contains("\"lowerBoundType\":\"OPEN\"");
      assertThat(json).doesNotContain("upperEndpoint");
    }

    @Test
    void serializeLessThanRange() throws JsonProcessingException {
      Range<Integer> range = Range.lessThan(100);
      String json = mapper.writeValueAsString(range);
      assertThat(json).contains("\"upperBoundType\":\"OPEN\"");
      assertThat(json).doesNotContain("lowerEndpoint");
    }

    @Test
    void jsonObjectIsValidJson() throws JsonProcessingException {
      Range<Integer> range = Range.closed(0, 100);
      String json = mapper.writeValueAsString(range);
      // Verify it starts and ends with braces (writeStartObject/writeEndObject)
      assertThat(json).startsWith("{");
      assertThat(json).endsWith("}");
      // Verify it can be parsed back
      mapper.readTree(json);
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
      Range<Integer> range =
          mapper.readValue("\"[0..100]\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.closed(0, 100));
    }

    @Test
    void deserializeOpenRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"(0..100)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.open(0, 100));
    }

    @Test
    void deserializeClosedOpenRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"[0..100)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void deserializeOpenClosedRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"(0..100]\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.openClosed(0, 100));
    }

    @Test
    void deserializeAtLeastRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"[0..+∞)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @Test
    void deserializeGreaterThanRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"(0..+∞)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.greaterThan(0));
    }

    @Test
    void deserializeAtMostRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"(-∞..100]\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.atMost(100));
    }

    @Test
    void deserializeLessThanRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"(-∞..100)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.lessThan(100));
    }

    @Test
    void deserializeAllRange() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"(-∞..+∞)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.all());
    }

    @Test
    void deserializeWithInfVariant() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"[0..+inf)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @Test
    void deserializeWithInfinityVariant() throws JsonProcessingException {
      Range<Integer> range =
          mapper.readValue("\"[0..+Infinity)\"", new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @Test
    void deserializeDoubleRange() throws JsonProcessingException {
      Range<Double> range =
          mapper.readValue("\"[0.5..1.5)\"", new TypeReference<Range<Double>>() {});
      assertThat(range).isEqualTo(Range.closedOpen(0.5, 1.5));
    }

    @Test
    void deserializeLongRange() throws JsonProcessingException {
      Range<Long> range =
          mapper.readValue("\"[0..9999999999]\"", new TypeReference<Range<Long>>() {});
      assertThat(range).isEqualTo(Range.closed(0L, 9999999999L));
    }

    @Test
    void deserializeStringRange() throws JsonProcessingException {
      Range<String> range = mapper.readValue("\"[a..z]\"", new TypeReference<Range<String>>() {});
      assertThat(range).isEqualTo(Range.closed("a", "z"));
    }

    @Test
    void deserializeLocalDateRange() throws JsonProcessingException {
      Range<LocalDate> range =
          mapper.readValue(
              "\"[2024-01-01..2024-12-31]\"", new TypeReference<Range<LocalDate>>() {});
      assertThat(range)
          .isEqualTo(Range.closed(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }

    @Test
    void deserializeDurationRange() throws JsonProcessingException {
      Range<Duration> range =
          mapper.readValue("\"[PT1H..PT24H)\"", new TypeReference<Range<Duration>>() {});
      assertThat(range).isEqualTo(Range.closedOpen(Duration.ofHours(1), Duration.ofHours(24)));
    }
  }

  @Nested
  class JsonObjectDeserialization {

    private final ObjectMapper mapper =
        new ObjectMapper().registerModule(new GuavaRangeParserModule());

    @Test
    void deserializeClosedRange() throws JsonProcessingException {
      String json =
          """
          {
            "lowerEndpoint": 0,
            "upperEndpoint": 100,
            "lowerBoundType": "CLOSED",
            "upperBoundType": "CLOSED"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.closed(0, 100));
    }

    @Test
    void deserializeOpenRange() throws JsonProcessingException {
      String json =
          """
          {
            "lowerEndpoint": 0,
            "upperEndpoint": 100,
            "lowerBoundType": "OPEN",
            "upperBoundType": "OPEN"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.open(0, 100));
    }

    @Test
    void deserializeAtLeastRange() throws JsonProcessingException {
      String json =
          """
          {
            "lowerEndpoint": 0,
            "lowerBoundType": "CLOSED"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.atLeast(0));
    }

    @Test
    void deserializeAtMostRange() throws JsonProcessingException {
      String json =
          """
          {
            "upperEndpoint": 100,
            "upperBoundType": "CLOSED"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.atMost(100));
    }

    @Test
    void deserializeAllRange() throws JsonProcessingException {
      String json = "{}";
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.all());
    }

    @Test
    void deserializeIgnoresUnknownFields() throws JsonProcessingException {
      String json =
          """
          {
            "lowerEndpoint": 0,
            "upperEndpoint": 100,
            "lowerBoundType": "CLOSED",
            "upperBoundType": "OPEN",
            "unknownField": "ignored"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void deserializeOpenClosedRange() throws JsonProcessingException {
      String json =
          """
          {
            "lowerEndpoint": 0,
            "upperEndpoint": 100,
            "lowerBoundType": "OPEN",
            "upperBoundType": "CLOSED"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.openClosed(0, 100));
    }

    @Test
    void deserializeClosedOpenRange() throws JsonProcessingException {
      String json =
          """
          {
            "lowerEndpoint": 0,
            "upperEndpoint": 100,
            "lowerBoundType": "CLOSED",
            "upperBoundType": "OPEN"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.closedOpen(0, 100));
    }

    @Test
    void deserializeGreaterThanRange() throws JsonProcessingException {
      String json =
          """
          {
            "lowerEndpoint": 0,
            "lowerBoundType": "OPEN"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.greaterThan(0));
    }

    @Test
    void deserializeLessThanRange() throws JsonProcessingException {
      String json =
          """
          {
            "upperEndpoint": 100,
            "upperBoundType": "OPEN"
          }
          """;
      Range<Integer> range = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(range).isEqualTo(Range.lessThan(100));
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
      Range<Integer> deserialized = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void roundTripAllRange() throws JsonProcessingException {
      Range<Integer> original = Range.all();
      String json = mapper.writeValueAsString(original);
      Range<Integer> deserialized = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void roundTripAtLeastRange() throws JsonProcessingException {
      Range<Integer> original = Range.atLeast(50);
      String json = mapper.writeValueAsString(original);
      Range<Integer> deserialized = mapper.readValue(json, new TypeReference<Range<Integer>>() {});
      assertThat(deserialized).isEqualTo(original);
    }

    @Test
    void roundTripDoubleRange() throws JsonProcessingException {
      Range<Double> original = Range.closedOpen(0.1, 0.9);
      String json = mapper.writeValueAsString(original);
      Range<Double> deserialized = mapper.readValue(json, new TypeReference<Range<Double>>() {});
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
}
