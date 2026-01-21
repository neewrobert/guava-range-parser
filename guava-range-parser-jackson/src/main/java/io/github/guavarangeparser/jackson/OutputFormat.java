package io.github.guavarangeparser.jackson;

/**
 * Defines the output format for serializing Guava Range objects.
 *
 * @see GuavaRangeParserModule
 */
public enum OutputFormat {

  /**
   * Serialize as string notation (e.g., "[0..100)", "(-∞..+∞)").
   *
   * <p>This produces compact, human-readable output.
   */
  STRING_NOTATION,

  /**
   * Serialize as JSON object with explicit fields.
   *
   * <p>Example:
   * <pre>{@code
   * {
   *   "lowerEndpoint": 0,
   *   "upperEndpoint": 100,
   *   "lowerBoundType": "CLOSED",
   *   "upperBoundType": "OPEN"
   * }
   * }</pre>
   *
   * <p>This format is more verbose but easier to process programmatically.
   */
  JSON_OBJECT
}