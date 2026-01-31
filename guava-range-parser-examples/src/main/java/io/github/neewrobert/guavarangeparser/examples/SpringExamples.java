package io.github.neewrobert.guavarangeparser.examples;

import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Examples demonstrating Spring Boot integration.
 *
 * <p>The Spring module provides:
 *
 * <ul>
 *   <li>Automatic conversion in {@code @ConfigurationProperties}
 *   <li>Support for {@code @Value} annotation injection
 *   <li>IDE autocomplete via spring-configuration-metadata.json
 * </ul>
 */
@Component
@Order(3)
public class SpringExamples implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(SpringExamples.class);

  private final AppProperties appProperties;

  /** Range injected via @Value annotation from application.properties. */
  @Value("${app.value-range:[0..100]}")
  private Range<Integer> valueRange;

  public SpringExamples(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Override
  public void run(String... args) {
    LOG.info("\n=== Spring Module Examples ===");

    configurationPropertiesExample();
    valueAnnotationExample();
    rangeOperationsExample();
  }

  /** Demonstrates @ConfigurationProperties binding. */
  private void configurationPropertiesExample() {
    LOG.info("\n--- @ConfigurationProperties Binding ---");

    LOG.info("app.stock-range: {}", appProperties.getStockRange());
    LOG.info("app.price-range: {}", appProperties.getPriceRange());
    LOG.info("app.timeout-range: {}", appProperties.getTimeoutRange());
    LOG.info("app.unbounded-range: {}", appProperties.getUnboundedRange());
  }

  /** Demonstrates @Value annotation injection. */
  private void valueAnnotationExample() {
    LOG.info("\n--- @Value Annotation Injection ---");

    LOG.info("@Value(\"${{app.value-range}}\") -> {}", valueRange);
    LOG.info("Contains 50? {}", valueRange.contains(50));
    LOG.info("Contains 150? {}", valueRange.contains(150));
  }

  /** Demonstrates using Range for business logic. */
  private void rangeOperationsExample() {
    LOG.info("\n--- Range Operations for Business Logic ---");

    Range<Integer> stockRange = appProperties.getStockRange();

    // Check if a quantity is valid
    int requestedQuantity = 500;
    boolean isValid = stockRange.contains(requestedQuantity);
    LOG.info("Is quantity {} valid? {} (stock range: {})", requestedQuantity, isValid, stockRange);

    // Check if ranges overlap
    Range<Integer> promotionRange = Range.closed(100, 200);
    boolean overlaps = !stockRange.intersection(promotionRange).isEmpty();
    LOG.info("Does {} overlap with {}? {}", stockRange, promotionRange, overlaps);

    // Get the intersection
    if (overlaps) {
      Range<Integer> intersection = stockRange.intersection(promotionRange);
      LOG.info("Intersection: {}", intersection);
    }
  }
}
