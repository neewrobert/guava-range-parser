package io.github.neewrobert.guavarangeparser.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.InfinityStyle;
import io.github.neewrobert.guavarangeparser.core.RangeFormatter;
import java.io.IOException;

/** Jackson serializer for Guava Range objects. */
class RangeSerializer extends JsonSerializer<Range<?>> {

  private final OutputFormat outputFormat;
  private final RangeFormatter formatter;

  RangeSerializer(OutputFormat outputFormat, InfinityStyle infinityStyle) {
    this.outputFormat = outputFormat;
    this.formatter = RangeFormatter.builder().infinityStyle(infinityStyle).build();
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void serialize(Range<?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    if (outputFormat == OutputFormat.STRING_NOTATION) {
      gen.writeString(formatter.format((Range) value));
    } else {
      writeAsJsonObject(value, gen);
    }
  }

  private void writeAsJsonObject(Range<?> range, JsonGenerator gen) throws IOException {
    gen.writeStartObject();

    if (range.hasLowerBound()) {
      gen.writeObjectField("lowerEndpoint", range.lowerEndpoint());
      gen.writeStringField("lowerBoundType", range.lowerBoundType().name());
    }

    if (range.hasUpperBound()) {
      gen.writeObjectField("upperEndpoint", range.upperEndpoint());
      gen.writeStringField("upperBoundType", range.upperBoundType().name());
    }

    gen.writeEndObject();
  }

  @Override
  public Class<Range<?>> handledType() {
    @SuppressWarnings("unchecked")
    Class<Range<?>> clazz = (Class<Range<?>>) (Class<?>) Range.class;
    return clazz;
  }
}
