package io.github.neewrobert.guavarangeparser.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.InfinityStyle;
import io.github.neewrobert.guavarangeparser.core.RangeFormatter;
import java.io.IOException;

/**
 * Jackson serializer for Guava Range objects.
 *
 * <p>Serializes Range objects to string notation (e.g., "[0..100)", "(-∞..+∞)").
 */
class RangeSerializer extends JsonSerializer<Range<?>> {

  private final RangeFormatter formatter;

  RangeSerializer(InfinityStyle infinityStyle) {
    this.formatter = RangeFormatter.builder().infinityStyle(infinityStyle).build();
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void serialize(Range<?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeString(formatter.format((Range) value));
  }

  @Override
  public Class<Range<?>> handledType() {
    @SuppressWarnings("unchecked")
    Class<Range<?>> clazz = (Class<Range<?>>) (Class<?>) Range.class;
    return clazz;
  }
}
