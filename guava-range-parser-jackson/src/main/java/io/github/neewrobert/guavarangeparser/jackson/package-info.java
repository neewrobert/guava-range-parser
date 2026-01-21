/**
 * Jackson module for Guava Range string notation serialization/deserialization.
 *
 * <p>This package provides integration with Jackson for serializing and deserializing Guava {@link
 * com.google.common.collect.Range} objects using standard mathematical interval notation.
 *
 * <h2>Quick Start</h2>
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
 * <h2>Configuration</h2>
 *
 * <pre>{@code
 * GuavaRangeParserModule module = GuavaRangeParserModule.builder()
 *     .outputFormat(OutputFormat.STRING_NOTATION)
 *     .infinityStyle(InfinityStyle.SYMBOL)
 *     .build();
 * }</pre>
 *
 * @see io.github.neewrobert.jackson.GuavaRangeParserModule
 * @see io.github.neewrobert.jackson.OutputFormat
 */
package io.github.neewrobert.guavarangeparser.jackson;
