package io.github.neewrobert.guavarangeparser.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main application demonstrating guava-range-parser features.
 *
 * <p>This application showcases:
 *
 * <ul>
 *   <li>Core parsing and formatting with {@link CoreExamples}
 *   <li>Jackson serialization with {@link JacksonExamples}
 *   <li>Spring Boot integration with {@link AppProperties}
 *   <li>Bean Validation with {@link ValidationExamples}
 * </ul>
 *
 * <p>Run with: {@code mvn spring-boot:run}
 */
@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class ExamplesApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExamplesApplication.class, args);
  }
}
