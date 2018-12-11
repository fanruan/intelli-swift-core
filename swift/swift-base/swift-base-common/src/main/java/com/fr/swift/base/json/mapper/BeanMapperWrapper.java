package com.fr.swift.base.json.mapper;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.base.json.annotation.JsonIgnoreProperties;
import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-04
 */
public abstract class BeanMapperWrapper implements BeanMapper {
    private Map<Class, Class> primitiveToWrapper = new HashMap<Class, Class>();
    private BeanMapper mapper;

    {
        primitiveToWrapper.put(Integer.TYPE, Integer.class);
        primitiveToWrapper.put(Boolean.TYPE, Boolean.class);
        primitiveToWrapper.put(Character.TYPE, Character.class);
        primitiveToWrapper.put(Long.TYPE, Long.class);
        primitiveToWrapper.put(Short.TYPE, Short.class);
        primitiveToWrapper.put(Double.TYPE, Double.class);
        primitiveToWrapper.put(Float.TYPE, Float.class);
        primitiveToWrapper.put(Byte.TYPE, Byte.class);
    }

    protected BeanMapperWrapper(BeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String writeValueAsString(Object o) throws Exception {
        StringBuffer buffer = new StringBuffer("{");
        buildClassFieldJson(buffer, o.getClass(), o);
        buffer.setLength(buffer.length() - 1);
        buffer.append("}");
        return buffer.toString();
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


    private void buildClassFieldJson(StringBuffer buffer, Class clazz, Object o) {
        List<String> ignoreProperties = new ArrayList<String>();
        if (clazz.isAnnotationPresent(JsonIgnoreProperties.class)) {
            JsonIgnoreProperties properties = (JsonIgnoreProperties) clazz.getAnnotation(JsonIgnoreProperties.class);
            ignoreProperties.addAll(Arrays.asList(properties.value()));
        }
        for (Field field : clazz.getDeclaredFields()) {
            String propertyName;
            if (field.isAnnotationPresent(JsonProperty.class)) {
                JsonProperty property = field.getAnnotation(JsonProperty.class);
                propertyName = property.value();
            } else {
                propertyName = field.getName();
            }
            if (ignoreProperties.contains(propertyName)) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object obj = field.get(o);
                if (null != obj) {
                    buffer.append("\"").append(propertyName).append("\": ").append(getString(obj)).append(",");
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        if (null != clazz.getSuperclass()) {
            buildClassFieldJson(buffer, clazz.getSuperclass(), o);
        }
    }

    /**
     * @param object
     * @return
     */
    private String getString(Object object) {
        if (object instanceof Date) {
            return String.valueOf(((Date) object).getTime());
        }
        Class clazz = object.getClass();
        if (primitiveToWrapper.containsKey(clazz) || primitiveToWrapper.containsValue(clazz)) {
            return object.toString();
        }
        StringBuffer buffer = new StringBuffer();
        if (object instanceof Collection) {
            buffer.append("[");
            for (Object o : ((Collection) object)) {
                buffer.append(getString(o)).append(",");
            }
            buffer.setLength(buffer.length() - 1);
            buffer.append("]");
            return buffer.toString();
        }
        if (object instanceof Enum) {
            return "\"" + ((Enum) object).name() + "\"";
        }
        buildClassFieldJson(buffer, clazz, object);
        if (buffer.length() > 0) {
            buffer.insert(0, "{");
            buffer.setLength(buffer.length() - 1);
            buffer.append("}");
            return buffer.toString();
        }
        return "\"" + object.toString() + "\"";
    }
}
