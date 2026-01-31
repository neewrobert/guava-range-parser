package io.github.neewrobert.guavarangeparser.spring.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Range;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RangeValidatorTest {

  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Nested
  class NullHandling {

    @Test
    void nullRangeIsValid() {
      TestBean bean = new TestBean();
      bean.simpleRange = null;

      Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);

      assertThat(violations).isEmpty();
    }
  }

  @Nested
  class NotEmptyConstraint {

    @Test
    void nonEmptyRangeIsValid() {
      NotEmptyBean bean = new NotEmptyBean();
      bean.range = Range.closed(0, 10);

      Set<ConstraintViolation<NotEmptyBean>> violations = validator.validate(bean);

      assertThat(violations).isEmpty();
    }

    @Test
    void emptyRangeIsInvalid() {
      NotEmptyBean bean = new NotEmptyBean();
      bean.range = Range.closedOpen(5, 5); // Empty range

      Set<ConstraintViolation<NotEmptyBean>> violations = validator.validate(bean);

      assertThat(violations).hasSize(1);
      assertThat(violations.iterator().next().getMessage()).contains("must not be empty");
    }

    @Test
    void singletonRangeIsValid() {
      NotEmptyBean bean = new NotEmptyBean();
      bean.range = Range.singleton(5); // Contains exactly one value

      Set<ConstraintViolation<NotEmptyBean>> violations = validator.validate(bean);

      assertThat(violations).isEmpty();
    }
  }

  @Nested
  class RequireLowerBoundConstraint {

    @Test
    void rangeWithLowerBoundIsValid() {
      LowerBoundBean bean = new LowerBoundBean();
      bean.range = Range.atLeast(0);

      Set<ConstraintViolation<LowerBoundBean>> violations = validator.validate(bean);

      assertThat(violations).isEmpty();
    }

    @Test
    void rangeWithoutLowerBoundIsInvalid() {
      LowerBoundBean bean = new LowerBoundBean();
      bean.range = Range.atMost(100); // No lower bound

      Set<ConstraintViolation<LowerBoundBean>> violations = validator.validate(bean);

      assertThat(violations).hasSize(1);
      assertThat(violations.iterator().next().getMessage()).contains("lower bound");
    }

    @Test
    void allRangeIsInvalid() {
      LowerBoundBean bean = new LowerBoundBean();
      bean.range = Range.all(); // No bounds

      Set<ConstraintViolation<LowerBoundBean>> violations = validator.validate(bean);

      assertThat(violations).hasSize(1);
    }
  }

  @Nested
  class RequireUpperBoundConstraint {

    @Test
    void rangeWithUpperBoundIsValid() {
      UpperBoundBean bean = new UpperBoundBean();
      bean.range = Range.atMost(100);

      Set<ConstraintViolation<UpperBoundBean>> violations = validator.validate(bean);

      assertThat(violations).isEmpty();
    }

    @Test
    void rangeWithoutUpperBoundIsInvalid() {
      UpperBoundBean bean = new UpperBoundBean();
      bean.range = Range.atLeast(0); // No upper bound

      Set<ConstraintViolation<UpperBoundBean>> violations = validator.validate(bean);

      assertThat(violations).hasSize(1);
      assertThat(violations.iterator().next().getMessage()).contains("upper bound");
    }
  }

  @Nested
  class MultipleConstraints {

    @Test
    void validBoundedNonEmptyRange() {
      BoundedBean bean = new BoundedBean();
      bean.range = Range.closed(0, 100);

      Set<ConstraintViolation<BoundedBean>> violations = validator.validate(bean);

      assertThat(violations).isEmpty();
    }

    @Test
    void unboundedRangeViolatesMultipleConstraints() {
      BoundedBean bean = new BoundedBean();
      bean.range = Range.all();

      Set<ConstraintViolation<BoundedBean>> violations = validator.validate(bean);

      // Should violate both requireLowerBound and requireUpperBound
      assertThat(violations).hasSize(2);
    }

    @Test
    void closedRangeIsValid() {
      BoundedBean bean = new BoundedBean();
      bean.range = Range.closed(10, 20);

      Set<ConstraintViolation<BoundedBean>> violations = validator.validate(bean);

      assertThat(violations).isEmpty();
    }
  }

  // Test beans

  static class TestBean {
    @ValidRange Range<Integer> simpleRange;
  }

  static class NotEmptyBean {
    @ValidRange(notEmpty = true)
    Range<Integer> range;
  }

  static class LowerBoundBean {
    @ValidRange(requireLowerBound = true)
    Range<Integer> range;
  }

  static class UpperBoundBean {
    @ValidRange(requireUpperBound = true)
    Range<Integer> range;
  }

  static class BoundedBean {
    @ValidRange(requireLowerBound = true, requireUpperBound = true)
    Range<Integer> range;
  }
}
