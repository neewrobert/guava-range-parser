package io.github.neewrobert.guavarangeparser.spring;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Auto-configuration for Guava Range converters in Spring Boot applications.
 *
 * <p>This configuration automatically registers a {@link RangeConverterFactory} bean that enables
 * conversion of String properties to Guava Range objects using standard mathematical interval
 * notation.
 *
 * <p>To use, simply add the dependency to your project:
 *
 * <pre>{@code
 * <dependency>
 *     <groupId>io.github.neewrobert</groupId>
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
 * <p>You can also use {@code @Value} annotations for direct injection:
 *
 * <pre>{@code
 * @Component
 * public class MyComponent {
 *     @Value("${my-app.stock-range}")
 *     private Range<Integer> stockRange;
 * }
 * }</pre>
 *
 * <p>To enable lenient mode (for backward compatibility with bracket-less notation):
 *
 * <pre>
 * guava.range-parser.lenient=true
 * </pre>
 *
 * @see RangeConverterFactory
 * @see RangeParserProperties
 */
@AutoConfiguration
@ConditionalOnClass(Range.class)
public class RangeConverterAutoConfiguration {

  /**
   * Registers the Range parser bean, allowing customization via properties.
   *
   * <p>Uses manual binding to avoid circular dependency with the converter factory, which is needed
   * by the configuration properties binding system.
   *
   * @param environment the Spring environment for property binding
   * @return the configured parser
   */
  @Bean
  @ConditionalOnMissingBean
  public RangeParser rangeParser(Environment environment) {
    RangeParserProperties properties =
        Binder.get(environment)
            .bind("guava.range-parser", RangeParserProperties.class)
            .orElseGet(RangeParserProperties::new);
    return RangeParser.builder().lenient(properties.isLenient()).build();
  }

  /**
   * Registers the Range converter factory for Spring configuration properties binding.
   *
   * <p>The converter is registered with both:
   *
   * <ul>
   *   <li>{@code @ConfigurationPropertiesBinding} - for {@code @ConfigurationProperties} binding
   *   <li>Environment's ConversionService - for {@code @Value} annotation support
   * </ul>
   *
   * @param parser the parser to use for conversion
   * @param environment the Spring environment for registering with its conversion service
   * @return the converter factory
   */
  @Bean
  @ConditionalOnMissingBean(RangeConverterFactory.class)
  @ConfigurationPropertiesBinding
  public RangeConverterFactory rangeConverterFactory(RangeParser parser, Environment environment) {
    RangeConverterFactory factory = new RangeConverterFactory(parser);

    // Register with Environment's ConversionService for @Value support
    if (environment instanceof ConfigurableEnvironment configurableEnv) {
      ConfigurableConversionService conversionService = configurableEnv.getConversionService();
      conversionService.addConverter(factory);
    }

    return factory;
  }
}
