package com.fr.swift.base.json.mapper;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-04
 */
public abstract class BeanMapperWrapper implements BeanMapper {
    private BeanMapper mapper;

    protected BeanMapperWrapper(BeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String writeValueAsString(Object o) throws Exception {
        return mapper.writeValueAsString(o);
    }

    @Override
    public Object readValue(String jsonString, BeanTypeReference reference) throws Exception {
        return mapper.readValue(jsonString, reference);
    }

    @Override
    public <T> T readValue(String jsonString, Class<T> reference) throws Exception {
        Class clazz = reference;
        if (ReflectUtils.isPrimitiveOrWrapper(reference)) {
            return (T) ReflectUtils.parseObject(reference, jsonString);
        }
        if (ReflectUtils.isAssignable(reference, String.class)) {
            return (T) jsonString.replace("\"", "");
        }
        if (ReflectUtils.isAssignable(reference, Map.class)) {
            return mapper.readValue(jsonString, reference);
        }
        Map map = mapper.readValue(jsonString, Map.class);
        if (reference.isAnnotationPresent(JsonTypeInfo.class)) {
            JsonTypeInfo info = reference.getAnnotation(JsonTypeInfo.class);
            String property = info.property();
            if (reference.isAnnotationPresent(JsonSubTypes.class)) {
                JsonSubTypes subTypes = reference.getAnnotation(JsonSubTypes.class);
                JsonSubTypes.Type[] types = subTypes.value();
                if (Strings.isEmpty(property)) {
                    clazz = info.defaultImpl();
                } else {
                    if (!map.containsKey(property)) {
                        throw new RuntimeException(jsonString + " not contains property " + property);
                    }
                    String value = (String) map.get(property);
                    for (JsonSubTypes.Type type : types) {
                        if (value.equals(type.name())) {
                            clazz = type.value();
                            break;
                        }
                    }
                }

            }
        }
        Constructor constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object o = constructor.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                field.setAccessible(true);
                field.set(o, JsonBuilder.readValue(writeValueAsString(map.get(jsonProperty.value())), field.getType()));
            }
        }
        return (T) o;
    }
}
