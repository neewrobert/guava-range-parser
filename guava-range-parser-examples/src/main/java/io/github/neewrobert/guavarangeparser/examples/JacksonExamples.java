package io.github.neewrobert.guavarangeparser.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.jackson.GuavaRangeParserModule;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Examples demonstrating Jackson serialization and deserialization.
 *
 * <p>The Jackson module provides:
 *
 * <ul>
 *   <li>Serialize Range objects to JSON string notation
 *   <li>Deserialize JSON string notation to Range objects
 *   <li>Support for Range in collections (List, Map, etc.)
 * </ul>
 *
 * <p>Note: For JSON object format like {@code {"lowerEndpoint":0,"upperEndpoint":100}}, use
 * jackson-datatype-guava instead.
 */
@Component
@Order(2)
public class JacksonExamples implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(JacksonExamples.class);

  @Override
  public void run(String... args) throws JsonProcessingException {
    log.info("\n=== Jackson Module Examples ===");

    basicSerialization();
    basicDeserialization();
    collectionsSupport();
    objectWithRangeFields();
  }

  /** Demonstrates serializing Range objects to JSON. */
  private void basicSerialization() throws JsonProcessingException {
    log.info("\n--- Basic Serialization ---");

    ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaRangeParserModule());

    // Serialize various range types
    String closedJson = mapper.writeValueAsString(Range.closed(0, 100));
    log.info("Range.closed(0, 100) -> {}", closedJson);

    String openJson = mapper.writeValueAsString(Range.open(0, 100));
    log.info("Range.open(0, 100) -> {}", openJson);

    String atLeastJson = mapper.writeValueAsString(Range.atLeast(0));
    log.info("Range.atLeast(0) -> {}", atLeastJson);

    String allJson = mapper.writeValueAsString(Range.all());
    log.info("Range.all() -> {}", allJson);
  }

  /** Demonstrates deserializing JSON to Range objects. */
  private void basicDeserialization() throws JsonProcessingException {
    log.info("\n--- Basic Deserialization ---");

    ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaRangeParserModule());

    // Deserialize with explicit type
    Range<Integer> closed =
        mapper.readValue("\"[0..100]\"", new TypeReference<Range<Integer>>() {});
    log.info("\"[0..100]\" -> {}", closed);

    Range<Double> doubleRange =
        mapper.readValue("\"[0.0..1.0)\"", new TypeReference<Range<Double>>() {});
    log.info("\"[0.0..1.0)\" -> {}", doubleRange);

    Range<Long> unbounded = mapper.readValue("\"(-∞..+∞)\"", new TypeReference<Range<Long>>() {});
    log.info("\"(-∞..+∞)\" -> {}", unbounded);
  }

  /** Demonstrates Range in collections. */
  private void collectionsSupport() throws JsonProcessingException {
    log.info("\n--- Collections Support ---");

    ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaRangeParserModule());

    // List of ranges
    List<Range<Integer>> ranges =
        List.of(Range.closed(0, 10), Range.closed(20, 30), Range.closed(40, 50));

    String json = mapper.writeValueAsString(ranges);
    log.info("List of ranges -> {}", json);

    // Deserialize back
    List<Range<Integer>> parsed =
        mapper.readValue(json, new TypeReference<List<Range<Integer>>>() {});
    log.info("Parsed back -> {}", parsed);
  }

  /** Demonstrates objects with Range fields. */
  private void objectWithRangeFields() throws JsonProcessingException {
    log.info("\n--- Objects with Range Fields ---");

    ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaRangeParserModule());

    // Create a product with price and quantity ranges
    Product product = new Product("Widget", Range.closed(9.99, 29.99), Range.closed(1, 100));

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);
    log.info("Product serialized:\n{}", json);

    // Deserialize back
    Product parsed = mapper.readValue(json, Product.class);
    log.info(
        "Product deserialized: name={}, price={}, quantity={}",
        parsed.name(),
        parsed.priceRange(),
        parsed.quantityRange());
  }

  /** Example record with Range fields. */
  public record Product(String name, Range<Double> priceRange, Range<Integer> quantityRange) {}
}
