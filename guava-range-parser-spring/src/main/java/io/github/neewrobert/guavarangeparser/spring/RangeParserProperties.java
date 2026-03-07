package io.github.neewrobert.guavarangeparser.spring;

/**
 * Configuration properties for the Guava Range parser.
 *
 * <p>Bound manually via {@link org.springframework.boot.context.properties.bind.Binder} in {@link
 * RangeConverterAutoConfiguration} to avoid a circular dependency with the converter, which is
 * needed by the configuration properties binding system itself.
 *
 * <p>Example configuration:
 *
 * <pre>
 * guava.range-parser.lenient=true
 * </pre>
 *
 * @see RangeConverterAutoConfiguration
 */
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
