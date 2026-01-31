package io.github.neewrobert.guavarangeparser.spring.validation;

import com.google.common.collect.Range;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ValidRange} constraint on Guava {@link Range} objects.
 *
 * <p>This validator checks:
 *
 * <ul>
 *   <li>{@code notEmpty} - the range must contain at least one value
 *   <li>{@code requireLowerBound} - the range must have a lower endpoint
 *   <li>{@code requireUpperBound} - the range must have an upper endpoint
 * </ul>
 *
 * <p>Null ranges are considered valid (use {@code @NotNull} to require non-null).
 *
 * @see ValidRange
 */
public class RangeValidator implements ConstraintValidator<ValidRange, Range<?>> {

  private boolean notEmpty;
  private boolean requireLowerBound;
  private boolean requireUpperBound;

  @Override
  public void initialize(ValidRange annotation) {
    this.notEmpty = annotation.notEmpty();
    this.requireLowerBound = annotation.requireLowerBound();
    this.requireUpperBound = annotation.requireUpperBound();
  }

  @Override
  public boolean isValid(Range<?> range, ConstraintValidatorContext context) {
    // Null is valid (use @NotNull for null check)
    if (range == null) {
      return true;
    }

    boolean valid = true;
    context.disableDefaultConstraintViolation();

    if (notEmpty && range.isEmpty()) {
      context
          .buildConstraintViolationWithTemplate("Range must not be empty")
          .addConstraintViolation();
      valid = false;
    }

    if (requireLowerBound && !range.hasLowerBound()) {
      context
          .buildConstraintViolationWithTemplate("Range must have a lower bound")
          .addConstraintViolation();
      valid = false;
    }

    if (requireUpperBound && !range.hasUpperBound()) {
      context
          .buildConstraintViolationWithTemplate("Range must have an upper bound")
          .addConstraintViolation();
      valid = false;
    }

    return valid;
  }
}
