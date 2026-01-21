package io.github.guavarangeparser.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Range;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @Configuration
  static class CustomConverterConfiguration {
    static final RangeConverterFactory CUSTOM_FACTORY = new RangeConverterFactory();

    @Bean
    RangeConverterFactory rangeConverterFactory() {
      return CUSTOM_FACTORY;
    }
  }
}
