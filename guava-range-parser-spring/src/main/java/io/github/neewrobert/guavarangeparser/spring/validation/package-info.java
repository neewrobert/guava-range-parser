/**
 * Bean Validation (JSR-380) support for Guava Range objects.
 *
 * <p>This package provides validation annotations and validators for use with Jakarta Bean
 * Validation (formerly javax.validation).
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @ConfigurationProperties(prefix = "my-app")
 * @Validated
 * public class MyProperties {
 *
 *     @NotNull
 *     @ValidRange(notEmpty = true, requireLowerBound = true)
 *     private Range<Integer> priceRange;
 * }
 * }</pre>
 *
 * @see io.github.neewrobert.guavarangeparser.spring.validation.ValidRange
 * @see io.github.neewrobert.guavarangeparser.spring.validation.RangeValidator
 */
package io.github.neewrobert.guavarangeparser.spring.validation;
