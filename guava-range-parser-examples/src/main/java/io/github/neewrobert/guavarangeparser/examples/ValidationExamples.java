package io.github.neewrobert.guavarangeparser.examples;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.spring.validation.ValidRange;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Examples demonstrating Bean Validation with @ValidRange.
 *
 * <p>The validation module provides:
 *
 * <ul>
 *   <li>{@code @ValidRange} annotation for Range fields
 *   <li>{@code notEmpty} - ensures range is not empty
 *   <li>{@code requireLowerBound} - ensures range has a lower bound
 *   <li>{@code requireUpperBound} - ensures range has an upper bound
 * </ul>
 */
@Component
@Order(4)
public class ValidationExamples implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(ValidationExamples.class);

  private final Validator validator;

  public ValidationExamples(Validator validator) {
    this.validator = validator;
  }

  @Override
  public void run(String... args) {
    log.info("\n=== Bean Validation Examples ===");

    validRangeExample();
    invalidRangeExamples();
    combinedValidationExample();
  }

  /** Demonstrates validation of a valid Range. */
  private void validRangeExample() {
    log.info("\n--- Valid Range Example ---");

    ProductConfig config = new ProductConfig();
    config.setPriceRange(Range.closed(10.0, 100.0));
    config.setQuantityRange(Range.closed(1, 1000));
    config.setDiscountRange(Range.closedOpen(0.0, 0.5));

    Set<ConstraintViolation<ProductConfig>> violations = validator.validate(config);

    if (violations.isEmpty()) {
      log.info("ProductConfig is valid: {}", config);
    } else {
      violations.forEach(v -> log.warn("Violation: {}", v.getMessage()));
    }
  }

  /** Demonstrates various validation failures. */
  private void invalidRangeExamples() {
    log.info("\n--- Invalid Range Examples ---");

    // Empty range violation
    log.info("Testing empty range [5..5):");
    ProductConfig emptyRange = new ProductConfig();
    emptyRange.setPriceRange(Range.closedOpen(5.0, 5.0)); // Empty!
    emptyRange.setQuantityRange(Range.closed(1, 100));
    emptyRange.setDiscountRange(Range.closed(0.0, 0.1));
    validateAndLog(emptyRange);

    // Missing lower bound violation
    log.info("\nTesting unbounded range (-∞..100]:");
    ProductConfig unboundedLower = new ProductConfig();
    unboundedLower.setPriceRange(Range.closed(10.0, 100.0));
    unboundedLower.setQuantityRange(Range.atMost(100)); // No lower bound!
    unboundedLower.setDiscountRange(Range.closed(0.0, 0.1));
    validateAndLog(unboundedLower);

    // Missing upper bound violation
    log.info("\nTesting unbounded range [0..+∞):");
    ProductConfig unboundedUpper = new ProductConfig();
    unboundedUpper.setPriceRange(Range.closed(10.0, 100.0));
    unboundedUpper.setQuantityRange(Range.closed(1, 100));
    unboundedUpper.setDiscountRange(Range.atLeast(0.0)); // No upper bound!
    validateAndLog(unboundedUpper);
  }

  /** Demonstrates combining @ValidRange with other constraints. */
  private void combinedValidationExample() {
    log.info("\n--- Combined Validation Example ---");

    // Null violation (from @NotNull)
    log.info("Testing null range:");
    ProductConfig nullRange = new ProductConfig();
    nullRange.setPriceRange(null); // Null!
    nullRange.setQuantityRange(Range.closed(1, 100));
    nullRange.setDiscountRange(Range.closed(0.0, 0.1));
    validateAndLog(nullRange);
  }

  private void validateAndLog(ProductConfig config) {
    Set<ConstraintViolation<ProductConfig>> violations = validator.validate(config);
    if (violations.isEmpty()) {
      log.info("  Valid: {}", config);
    } else {
      violations.forEach(
          v -> log.info("  Violation on '{}': {}", v.getPropertyPath(), v.getMessage()));
    }
  }

  /** Example configuration class with validated Range fields. */
  public static class ProductConfig {

    @NotNull(message = "Price range is required")
    @ValidRange(notEmpty = true, requireLowerBound = true, requireUpperBound = true)
    private Range<Double> priceRange;

    @ValidRange(requireLowerBound = true, requireUpperBound = true)
    private Range<Integer> quantityRange;

    @ValidRange(requireUpperBound = true)
    private Range<Double> discountRange;

    public Range<Double> getPriceRange() {
      return priceRange;
    }

    public void setPriceRange(Range<Double> priceRange) {
      this.priceRange = priceRange;
    }

    public Range<Integer> getQuantityRange() {
      return quantityRange;
    }

    public void setQuantityRange(Range<Integer> quantityRange) {
      this.quantityRange = quantityRange;
    }

    public Range<Double> getDiscountRange() {
      return discountRange;
    }

    public void setDiscountRange(Range<Double> discountRange) {
      this.discountRange = discountRange;
    }

    @Override
    public String toString() {
      return "ProductConfig{"
          + "priceRange="
          + priceRange
          + ", quantityRange="
          + quantityRange
          + ", discountRange="
          + discountRange
          + '}';
    }
  }
}
