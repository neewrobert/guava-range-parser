package io.github.guavarangeparser.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.Range;
import io.github.guavarangeparser.core.RangeParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

class RangeConverterAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(RangeConverterAutoConfiguration.class));

  @Test
  void autoConfigurationCreatesConverterFactory() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(RangeConverterFactory.class);
        });
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
        context -> {
          assertThat(context).hasSingleBean(RangeParser.class);
        });
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
}
