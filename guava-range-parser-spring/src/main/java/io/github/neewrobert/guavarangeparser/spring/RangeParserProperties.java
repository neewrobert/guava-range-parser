package io.github.neewrobert.guavarangeparser.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Guava Range parser.
 *
 * <p>Example configuration:
 *
 * <pre>
 * guava.range-parser.lenient=true
 * </pre>
 *
 * @see RangeConverterAutoConfiguration
 */
@ConfigurationProperties(prefix = "guava.range-parser")
public class RangeParserProperties {

  /**
   * Enable lenient parsing mode.
   *
   * <p>When enabled, bracket-less notation like "0..100" is accepted and treated as closed-open
   * ranges (equivalent to "[0..100)"). This is useful for backward compatibility with existing
   * property files.
   *
   * <p>Default: false (strict mode requiring explicit brackets)
   */
  private boolean lenient = false;

  public boolean isLenient() {
    return lenient;
  }

  public void setLenient(boolean lenient) {
    this.lenient = lenient;
  }
}
