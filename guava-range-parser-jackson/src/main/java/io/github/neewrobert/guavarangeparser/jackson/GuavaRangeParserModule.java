package io.github.neewrobert.guavarangeparser.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import io.github.neewrobert.guavarangeparser.core.InfinityStyle;

/**
 * Jackson module for Guava Range string notation serialization/deserialization.
 *
 * <p>This module allows Jackson to serialize and deserialize Guava Range objects using the standard
 * mathematical interval notation (e.g., "[0..100)", "(-∞..+∞)").
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * ObjectMapper mapper = new ObjectMapper()
 *     .registerModule(new GuavaRangeParserModule());
 *
 * // Deserialize from string notation
 * Range<Integer> range = mapper.readValue("\"[0..100)\"", new TypeReference<Range<Integer>>() {});
 *
 * // Serialize to string notation
 * String json = mapper.writeValueAsString(Range.closedOpen(0, 100));
 * // Returns: "[0..100)"
 * }</pre>
 *
 * <p>For JSON object format (e.g., {@code {"lowerEndpoint":0,"upperEndpoint":100,...}}), use
 * Jackson's {@code jackson-datatype-guava} module instead.
 *
 * @see io.github.neewrobert.guavarangeparser.core.RangeParser
 * @see io.github.neewrobert.guavarangeparser.core.RangeFormatter
 */
public class GuavaRangeParserModule extends Module {

  private static final String MODULE_NAME = "guava-range-parser";

  private final InfinityStyle infinityStyle;

  /** Creates a new module with default settings. */
  public GuavaRangeParserModule() {
    this(InfinityStyle.SYMBOL);
  }

  private GuavaRangeParserModule(InfinityStyle infinityStyle) {
    this.infinityStyle = infinityStyle;
  }

  /**
   * Creates a new builder for configuring the module.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String getModuleName() {
    return MODULE_NAME;
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext context) {
    context.addSerializers(new RangeSerializers(infinityStyle));
    context.addDeserializers(new RangeDeserializers());
  }

  /**
   * Returns the configured infinity style.
   *
   * @return the infinity style
   */
  public InfinityStyle getInfinityStyle() {
    return infinityStyle;
  }

  /** Builder for creating configured {@link GuavaRangeParserModule} instances. */
  public static final class Builder {
    private InfinityStyle infinityStyle = InfinityStyle.SYMBOL;

    private Builder() {}

    /**
     * Sets the infinity style for output.
     *
     * @param infinityStyle the infinity style
     * @return this builder
     */
    public Builder infinityStyle(InfinityStyle infinityStyle) {
      this.infinityStyle = infinityStyle;
      return this;
    }

    /**
     * Builds the configured module.
     *
     * @return a new module instance
     */
    public GuavaRangeParserModule build() {
      return new GuavaRangeParserModule(infinityStyle);
    }
  }
}
