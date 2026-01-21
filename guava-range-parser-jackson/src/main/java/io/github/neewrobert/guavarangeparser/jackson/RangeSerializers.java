package io.github.neewrobert.guavarangeparser.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.common.collect.Range;
import io.github.neewrobert.guavarangeparser.core.InfinityStyle;

/** Jackson serializers provider for Guava Range types. */
class RangeSerializers extends Serializers.Base {

  private final OutputFormat outputFormat;
  private final InfinityStyle infinityStyle;

  RangeSerializers(OutputFormat outputFormat, InfinityStyle infinityStyle) {
    this.outputFormat = outputFormat;
    this.infinityStyle = infinityStyle;
  }

  @Override
  public JsonSerializer<?> findSerializer(
      SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    if (Range.class.isAssignableFrom(type.getRawClass())) {
      return new RangeSerializer(outputFormat, infinityStyle);
    }
    return null;
  }
}
