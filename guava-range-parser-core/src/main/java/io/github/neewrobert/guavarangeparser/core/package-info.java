/**
 * Core parsing and formatting functionality for Guava Range objects.
 *
 * <p>This package provides the main API for converting between string notation and Guava {@link
 * com.google.common.collect.Range} objects:
 *
 * <ul>
 *   <li>{@link io.github.neewrobert.core.RangeParser} - Parses string notation to Range
 *   <li>{@link io.github.neewrobert.core.RangeFormatter} - Formats Range to string notation
 *   <li>{@link io.github.neewrobert.core.TypeAdapter} - Interface for custom type parsing
 *   <li>{@link io.github.neewrobert.core.BuiltInTypeAdapters} - Pre-configured adapters
 * </ul>
 *
 * <h2>Quick Start</h2>
 *
 * <pre>{@code
 * // Parse a range
 * Range<Integer> range = RangeParser.parse("[0..100)", Integer.class);
 *
 * // Format a range
 * String notation = RangeFormatter.toString(Range.closedOpen(0, 100));
 * }</pre>
 *
 * <h2>Supported Notation</h2>
 *
 * <table>
 *   <tr><th>Notation</th><th>Range Type</th><th>Description</th></tr>
 *   <tr><td>{@code [a..b]}</td><td>closed</td><td>Both endpoints inclusive</td></tr>
 *   <tr><td>{@code (a..b)}</td><td>open</td><td>Both endpoints exclusive</td></tr>
 *   <tr><td>{@code [a..b)}</td><td>closedOpen</td><td>Lower inclusive, upper exclusive</td></tr>
 *   <tr><td>{@code (a..b]}</td><td>openClosed</td><td>Lower exclusive, upper inclusive</td></tr>
 *   <tr><td>{@code [a..+∞)}</td><td>atLeast</td><td>Lower bounded, no upper bound</td></tr>
 *   <tr><td>{@code (a..+∞)}</td><td>greaterThan</td><td>Greater than a</td></tr>
 *   <tr><td>{@code (-∞..b]}</td><td>atMost</td><td>Upper bounded, no lower bound</td></tr>
 *   <tr><td>{@code (-∞..b)}</td><td>lessThan</td><td>Less than b</td></tr>
 *   <tr><td>{@code (-∞..+∞)}</td><td>all</td><td>Unbounded</td></tr>
 * </table>
 *
 * @see io.github.neewrobert.core.RangeParser
 * @see io.github.neewrobert.core.RangeFormatter
 */
package io.github.neewrobert.guavarangeparser.core;
