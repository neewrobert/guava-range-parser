package io.github.neewrobert.guavarangeparser.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

class RangeConverterAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(RangeConverterAutoConfiguration.class));

  @Test
  void autoConfigurationCreatesConverterFactory() {
    contextRunner.run(
        context -> assertThat(context).hasSingleBean(RangeConverterFactory.class));
  }

  @Test
  void converterFactoryIsConfigurationPropertiesBinding() {
    contextRunner.run(
        context -> {
          RangeConverterFactory factory = context.getBean(RangeConverterFactory.class);
          assertThat(factory).isNotNull();
        });
  }

  @Test
  void doesNotOverrideUserDefinedBean() {
    contextRunner
        .withUserConfiguration(CustomConverterConfiguration.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(RangeConverterFactory.class);
              assertThat(context.getBean(RangeConverterFactory.class))
                  .isSameAs(CustomConverterConfiguration.CUSTOM_FACTORY);
            });
  }

  @Test
  void converterFactoryCanConvertRanges() {
    contextRunner.run(
        context -> {
          RangeConverterFactory factory = context.getBean(RangeConverterFactory.class);
          var pairs = factory.getConvertibleTypes();
          assertThat(pairs).isNotEmpty();
          var pair = pairs.iterator().next();
          assertThat(pair.getSourceType()).isEqualTo(String.class);
          assertThat(pair.getTargetType()).isEqualTo(Range.class);
        });
  }

  @Test
  void autoConfigurationCreatesRangeParserBean() {
    contextRunner.run(
        context -> assertThat(context).hasSingleBean(RangeParser.class));
  }

  @Test
  void strictModeByDefault() {
    contextRunner.run(
        context -> {
          RangeConverterFactory factory = context.getBean(RangeConverterFactory.class);
          TypeDescriptor targetType =
              new TypeDescriptor(
                  ResolvableType.forClassWithGenerics(Range.class, Integer.class), null, null);

          // Bracket-less notation should fail in strict mode
          assertThatThrownBy(
                  () -> factory.convert("0..100", TypeDescriptor.valueOf(String.class), targetType))
              .isInstanceOf(IllegalArgumentException.class);
        });
  }

  @Test
  void lenientModeWhenEnabled() {
    contextRunner
        .withPropertyValues("guava.range-parser.lenient=true")
        .run(
            context -> {
              RangeConverterFactory factory = context.getBean(RangeConverterFactory.class);
              TypeDescriptor targetType =
                  new TypeDescriptor(
                      ResolvableType.forClassWithGenerics(Range.class, Integer.class), null, null);

              // Bracket-less notation should work in lenient mode
              Object result =
                  factory.convert("0..100", TypeDescriptor.valueOf(String.class), targetType);
              assertThat(result).isEqualTo(Range.closedOpen(0, 100));
            });
  }

  @Test
  void doesNotOverrideUserDefinedRangeParser() {
    contextRunner
        .withUserConfiguration(CustomParserConfiguration.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(RangeParser.class);
              assertThat(context.getBean(RangeParser.class))
                  .isSameAs(CustomParserConfiguration.CUSTOM_PARSER);
            });
  }

  @Test
  void converterIsRegisteredWithEnvironmentConversionService() {
    contextRunner.run(
        context -> {
          var conversionService = context.getEnvironment().getConversionService();

          // Verify the conversion service can convert String to Range
          assertThat(conversionService.canConvert(String.class, Range.class)).isTrue();

          // Verify actual conversion works (for @Value support)
          // Must use TypeDescriptor to specify the generic type
          TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
          TypeDescriptor targetType =
              new TypeDescriptor(
                  ResolvableType.forClassWithGenerics(Range.class, Integer.class), null, null);
          Object result = conversionService.convert("[0..100)", sourceType, targetType);
          assertThat(result).isEqualTo(Range.closedOpen(0, 100));
        });
  }

  @Configuration
  static class CustomConverterConfiguration {
    static final RangeConverterFactory CUSTOM_FACTORY = new RangeConverterFactory();

    @Bean
    RangeConverterFactory rangeConverterFactory() {
      return CUSTOM_FACTORY;
    }
  }

  @Configuration
  static class CustomParserConfiguration {
    static final RangeParser CUSTOM_PARSER = RangeParser.builder().lenient(true).build();

    @Bean
    RangeParser rangeParser() {
      return CUSTOM_PARSER;
    }
  }

  @Test
  void factoryCreatedWhenEnvironmentIsNotConfigurable() {
    // Test the false branch of instanceof ConfigurableEnvironment
    // This simulates a scenario where Environment is not a ConfigurableEnvironment
    RangeConverterAutoConfiguration config = new RangeConverterAutoConfiguration();
    RangeParser parser = RangeParser.builder().build();

    // Create a basic Environment that is not ConfigurableEnvironment
    Environment environment =
        new Environment() {
          @Override
          public String[] getActiveProfiles() {
            return new String[0];
          }

          @Override
          public String[] getDefaultProfiles() {
            return new String[0];
          }

          @Override
          public boolean acceptsProfiles(Profiles profiles) {
            return false;
          }

          @Override
          @Deprecated
          public boolean acceptsProfiles(String... profiles) {
            return false;
          }

          @Override
          public boolean containsProperty(String key) {
            return false;
          }

          @Override
          public String getProperty(String key) {
            return null;
          }

          @Override
          public String getProperty(String key, String defaultValue) {
            return defaultValue;
          }

          @Override
          public <T> T getProperty(String key, Class<T> targetType) {
            return null;
          }

          @Override
          public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
            return defaultValue;
          }

          @Override
          public String getRequiredProperty(String key) throws IllegalStateException {
            throw new IllegalStateException("Property not found: " + key);
          }

          @Override
          public <T> T getRequiredProperty(String key, Class<T> targetType)
              throws IllegalStateException {
            throw new IllegalStateException("Property not found: " + key);
          }

          @Override
          public String resolvePlaceholders(String text) {
            return text;
          }

          @Override
          public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
            return text;
          }
        };

    // Call the method - should return factory without registering with conversion service
    RangeConverterFactory factory = config.rangeConverterFactory(parser, environment);

    // Verify factory was created
    assertThat(factory).isNotNull();
    assertThat(factory.getConvertibleTypes()).isNotEmpty();
  }
}
