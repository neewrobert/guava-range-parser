package io.github.guavarangeparser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import io.github.guavarangeparser.core.RangeParseException;
import io.github.guavarangeparser.core.RangeParser;
import java.io.IOException;

/**
 * Jackson deserializer for Guava Range objects.
 *
 * <p>Supports both string notation (e.g., "[0..100)") and JSON object format.
 */
class RangeDeserializer extends JsonDeserializer<Range<?>> implements ContextualDeserializer {

  private final JavaType elementType;
  private final RangeParser parser;

  RangeDeserializer(JavaType elementType) {
    this.elementType = elementType;
    this.parser = RangeParser.builder().build();
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    JavaType type = ctxt.getContextualType();
    if (type != null && type.containedTypeCount() > 0) {
      return new RangeDeserializer(type.containedType(0));
    }
    return this;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public Range<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonToken token = p.currentToken();

    if (token == JsonToken.VALUE_STRING) {
      return deserializeFromString(p, ctxt);
    } else if (token == JsonToken.START_OBJECT) {
      return deserializeFromObject(p, ctxt);
    }

    return (Range<?>) ctxt.handleUnexpectedToken(Range.class, p);
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<T>> Range<T> deserializeFromString(
      JsonParser p, DeserializationContext ctxt) throws IOException {
    String notation = p.getText();
    Class<T> rawClass = (Class<T>) elementType.getRawClass();

    try {
      return parser.parseRange(notation, rawClass);
    } catch (RangeParseException e) {
      return (Range<T>) ctxt.handleWeirdStringValue(Range.class, notation, e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Comparable<T>> Range<T> deserializeFromObject(
      JsonParser p, DeserializationContext ctxt) throws IOException {
    T lowerEndpoint = null;
    T upperEndpoint = null;
    BoundType lowerBoundType = null;
    BoundType upperBoundType = null;

    while (p.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = p.currentName();
      p.nextToken();

      switch (fieldName) {
        case "lowerEndpoint":
          lowerEndpoint = (T) ctxt.readValue(p, elementType);
          break;
        case "upperEndpoint":
          upperEndpoint = (T) ctxt.readValue(p, elementType);
          break;
        case "lowerBoundType":
          lowerBoundType = BoundType.valueOf(p.getText());
          break;
        case "upperBoundType":
          upperBoundType = BoundType.valueOf(p.getText());
          break;
        default:
          p.skipChildren();
      }
    }

    return buildRange(lowerEndpoint, upperEndpoint, lowerBoundType, upperBoundType);
  }

  private <T extends Comparable<T>> Range<T> buildRange(
      T lower, T upper, BoundType lowerBound, BoundType upperBound) {
    boolean hasLower = lower != null && lowerBound != null;
    boolean hasUpper = upper != null && upperBound != null;

    if (!hasLower && !hasUpper) {
      return Range.all();
    }

    if (!hasLower) {
      return upperBound == BoundType.CLOSED ? Range.atMost(upper) : Range.lessThan(upper);
    }

    if (!hasUpper) {
      return lowerBound == BoundType.CLOSED ? Range.atLeast(lower) : Range.greaterThan(lower);
    }

    if (lowerBound == BoundType.CLOSED && upperBound == BoundType.CLOSED) {
      return Range.closed(lower, upper);
    } else if (lowerBound == BoundType.CLOSED && upperBound == BoundType.OPEN) {
      return Range.closedOpen(lower, upper);
    } else if (lowerBound == BoundType.OPEN && upperBound == BoundType.CLOSED) {
      return Range.openClosed(lower, upper);
    } else {
      return Range.open(lower, upper);
    }
  }
}
