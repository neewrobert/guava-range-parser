package io.github.neewrobert.guavarangeparser.examples;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.spring.validation.ValidRange;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties demonstrating Range binding in Spring Boot.
 *
 * <p>These properties are automatically converted from string notation in application.properties:
 *
 * <pre>
 * app.stock-range=[0..1000]
 * app.price-range=[0.01..9999.99]
 * app.timeout-range=[PT1S..PT30S]
 * </pre>
 *
 * <p>The {@code @Validated} annotation enables Bean Validation on this class.
 */
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

  /**
   * Valid range for stock quantities.
   *
   * <p>Must be non-null, non-empty, and bounded on both ends.
   */
  @NotNull
  @ValidRange(notEmpty = true, requireLowerBound = true, requireUpperBound = true)
  private Range<Integer> stockRange;

  /**
   * Valid range for product prices.
   *
   * <p>Must be non-null and have a lower bound (minimum price).
   */
  @NotNull
  @ValidRange(requireLowerBound = true)
  private Range<Double> priceRange;

  /** Valid range for operation timeouts. */
  private Range<Duration> timeoutRange;

  /** Example of an unbounded range (no validation constraints). */
  private Range<Long> unboundedRange;

  public Range<Integer> getStockRange() {
    return stockRange;
  }

  public void setStockRange(Range<Integer> stockRange) {
    this.stockRange = stockRange;
  }

  public Range<Double> getPriceRange() {
    return priceRange;
  }

  public void setPriceRange(Range<Double> priceRange) {
    this.priceRange = priceRange;
  }

  public Range<Duration> getTimeoutRange() {
    return timeoutRange;
  }

  public void setTimeoutRange(Range<Duration> timeoutRange) {
    this.timeoutRange = timeoutRange;
  }

  public Range<Long> getUnboundedRange() {
    return unboundedRange;
  }

  public void setUnboundedRange(Range<Long> unboundedRange) {
    this.unboundedRange = unboundedRange;
  }

  @Override
  public String toString() {
    return "AppProperties{"
        + "stockRange="
        + stockRange
        + ", priceRange="
        + priceRange
        + ", timeoutRange="
        + timeoutRange
        + ", unboundedRange="
        + unboundedRange
        + '}';
  }
}
