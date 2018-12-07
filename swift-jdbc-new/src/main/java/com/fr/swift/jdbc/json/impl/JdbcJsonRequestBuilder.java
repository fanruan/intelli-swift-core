package com.fr.swift.jdbc.json.impl;

import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.json.JsonRequestBuilder;
import com.fr.swift.api.json.annotation.ApiJsonProperty;
import com.fr.swift.jdbc.exception.Exceptions;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-03
 */
public class JdbcJsonRequestBuilder implements JsonRequestBuilder {
    private Map<Class, Class> primitiveToWrapper = new HashMap<Class, Class>();

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

    private static JdbcJsonRequestBuilder BUILDER = new JdbcJsonRequestBuilder();

    public static JsonRequestBuilder getInstance() {
        return BUILDER;
    }

    @Override
    public String buildRequest(RequestInfo requestInfo) {
        StringBuffer buffer = new StringBuffer("{");
        buildClassFieldJson(buffer, requestInfo.getClass(), requestInfo);
        buffer.setLength(buffer.length() - 1);
        buffer.append("}");
        return buffer.toString();
    }

    private void buildClassFieldJson(StringBuffer buffer, Class clazz, Object o) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ApiJsonProperty.class)) {
                ApiJsonProperty property = field.getAnnotation(ApiJsonProperty.class);
                String propertyName = property.value();
                try {
                    field.setAccessible(true);
                    Object obj = field.get(o);
                    if (null != obj) {
                        buffer.append("\"").append(propertyName).append("\": ").append(getString(obj)).append(",");
                    } else if (property.require()) {
                        throw Exceptions.runtime(String.format("%s is require", propertyName));
                    }
                } catch (IllegalAccessException ignore) {
                }
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
