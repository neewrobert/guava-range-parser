package io.github.guavarangeparser.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Range;

/**
 * Auto-configuration for Guava Range converters in Spring Boot applications.
 *
 * <p>This configuration automatically registers a {@link RangeConverterFactory} bean
 * that enables conversion of String properties to Guava Range objects using standard
 * mathematical interval notation.
 *
 * <p>To use, simply add the dependency to your project:
 *
 * <pre>{@code
 * <dependency>
 *     <groupId>io.github.guavarangeparser</groupId>
 *     <artifactId>guava-range-parser-spring</artifactId>
 * </dependency>
 * }</pre>
 *
 * <p>Then use Range types in your configuration properties:
 *
 * <pre>{@code
 * @ConfigurationProperties(prefix = "my-app")
 * public class MyProperties {
 *     private Range<Integer> stockRange;
 * }
 * }</pre>
 *
 * <pre>
 * my-app.stock-range=[0..100)
 * </pre>
 *
 * @see RangeConverterFactory
 */
@AutoConfiguration
@ConditionalOnClass(Range.class)
public class RangeConverterAutoConfiguration {

  /**
   * Registers the Range converter factory for Spring configuration properties binding.
   *
   * @return the converter factory
   */
  @Bean
  @ConfigurationPropertiesBinding
  @ConditionalOnMissingBean(RangeConverterFactory.class)
  public RangeConverterFactory rangeConverterFactory() {
    return new RangeConverterFactory();
  }
}