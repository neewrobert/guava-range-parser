package io.github.neewrobert.guavarangeparser.spring.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Validates that a Guava {@link com.google.common.collect.Range} meets specified constraints.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @ConfigurationProperties(prefix = "my-app")
 * public class MyProperties {
 *
 *     @ValidRange(notEmpty = true, requireLowerBound = true)
 *     private Range<Integer> priceRange;
 *
 *     @ValidRange(requireUpperBound = true)
 *     private Range<Duration> timeout;
 * }
 * }</pre>
 *
 * @see RangeValidator
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = RangeValidator.class)
@Documented
public @interface ValidRange {

  /**
   * The error message template.
   *
   * @return the error message
   */
  String message() default
      "{io.github.neewrobert.guavarangeparser.spring.validation.ValidRange.message}";

  /**
   * The validation groups.
   *
   * @return the groups
   */
  Class<?>[] groups() default {};

  /**
   * The payload for clients.
   *
   * @return the payload
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * Whether the range must not be empty. An empty range is one where the lower bound equals the
   * upper bound for closed endpoints, resulting in no values being contained.
   *
   * <p>Default: false
   *
   * @return true if the range must not be empty
   */
  boolean notEmpty() default false;

  /**
   * Whether the range must have a lower bound. If true, ranges like {@code (-∞..100]} will be
   * rejected.
   *
   * <p>Default: false
   *
   * @return true if a lower bound is required
   */
  boolean requireLowerBound() default false;

  /**
   * Whether the range must have an upper bound. If true, ranges like {@code [0..+∞)} will be
   * rejected.
   *
   * <p>Default: false
   *
   * @return true if an upper bound is required
   */
  boolean requireUpperBound() default false;
}
