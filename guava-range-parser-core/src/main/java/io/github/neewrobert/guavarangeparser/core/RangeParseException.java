package io.github.neewrobert.guavarangeparser.core;

import java.io.Serial;

/**
 * Exception thrown when a range string cannot be parsed.
 *
 * <p>This exception provides detailed information about the parsing failure, including the original
 * input string and the position where the error occurred.
 *
 * @see RangeParser
 */
public final class RangeParseException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  private final String input;
  private final int position;

  /**
   * Constructs a new RangeParseException.
   *
   * @param message the detail message
   * @param input the input string that failed to parse
   * @param position the position in the input where the error occurred
   */
  public RangeParseException(String message, String input, int position) {
    super(formatMessage(message, input, position));
    this.input = input;
    this.position = position;
  }

  /**
   * Constructs a new RangeParseException with a cause.
   *
   * @param message the detail message
   * @param input the input string that failed to parse
   * @param position the position in the input where the error occurred
   * @param cause the cause of the exception
   */
  public RangeParseException(String message, String input, int position, Throwable cause) {
    super(formatMessage(message, input, position), cause);
    this.input = input;
    this.position = position;
  }

  /**
   * Returns the input string that failed to parse.
   *
   * @return the input string
   */
  public String getInput() {
    return input;
  }

  /**
   * Returns the position in the input where the error occurred.
   *
   * @return the error position (0-based index)
   */
  public int getPosition() {
    return position;
  }

  private static String formatMessage(String message, String input, int position) {
    StringBuilder sb = new StringBuilder();
    sb.append(message);
    sb.append("\n  Input: \"").append(input).append("\"");
    if (position > 0 && position < input.length()) {
      sb.append("\n         ");
      sb.append(" ".repeat(position));
      sb.append("^");
    }
    return sb.toString();
  }
}
