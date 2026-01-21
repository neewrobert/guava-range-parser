package io.github.guavarangeparser.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.common.collect.Range;

/** Jackson deserializers provider for Guava Range types. */
class RangeDeserializers extends Deserializers.Base {

  @Override
  public JsonDeserializer<?> findBeanDeserializer(
      JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
    if (Range.class.isAssignableFrom(type.getRawClass())) {
      JavaType elementType = type.containedType(0);
      if (elementType == null) {
        elementType = config.getTypeFactory().constructType(Object.class);
      }
      return new RangeDeserializer(elementType);
    }
    return null;
  }
}
