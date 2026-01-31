package io.github.neewrobert.guavarangeparser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.RangeParseException;
import io.github.neewrobert.guavarangeparser.core.RangeParser;
import java.io.IOException;

/**
 * Jackson deserializer for Guava Range objects.
 *
 * <p>Deserializes Range objects from string notation (e.g., "[0..100)", "(-∞..+∞)").
 *
 * <p>For JSON object format deserialization, use Jackson's {@code jackson-datatype-guava} module.
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
  public Range<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonToken token = p.currentToken();

    if (token == JsonToken.VALUE_STRING) {
      return deserializeFromString(p, ctxt);
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
}
