/**
 * Spring Boot auto-configuration for Guava Range converters.
 *
 * <p>This package provides seamless integration with Spring Boot for converting String properties
 * to Guava {@link com.google.common.collect.Range} objects.
 *
 * <h2>Quick Start</h2>
 *
 * <p>Add the dependency to your project and Range properties will work automatically:
 *
 * <pre>{@code
 * @ConfigurationProperties(prefix = "my-app")
 * public class MyProperties {
 *     private Range<Integer> stockRange;      // [0..100)
 *     private Range<Duration> refreshInterval; // [PT1M..PT5M]
 * }
 * }</pre>
 *
 * <pre>
 * my-app.stock-range=[0..100)
 * my-app.refresh-interval=[PT1M..PT5M]
 * </pre>
 *
 * @see io.github.guavarangeparser.spring.RangeConverterFactory
 * @see io.github.guavarangeparser.spring.RangeConverterAutoConfiguration
 */
package io.github.guavarangeparser.spring;
