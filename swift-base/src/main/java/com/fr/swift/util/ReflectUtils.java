package com.fr.swift.util;

import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.org.apache.commons.lang3.ClassUtils;
import com.fr.third.org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

/**
 * @author yee
 * @date 2018/7/16
 */
public final class ReflectUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void set(Field field, Object object, String fieldValue) throws Exception {
        field.setAccessible(true);
        Class clazz = field.getType();
        if (ClassUtils.isPrimitiveOrWrapper(clazz)) {
            clazz = ClassUtils.isPrimitiveWrapper(clazz) ? clazz : ClassUtils.primitiveToWrapper(clazz);
            if (ClassUtils.isAssignable(clazz, Integer.class)) {
                field.set(object, Integer.parseInt(fieldValue));
            }
            if (ClassUtils.isAssignable(clazz, Byte.class)) {
                field.set(object, Byte.parseByte(fieldValue));
            }
            if (ClassUtils.isAssignable(clazz, Short.class)) {
                field.set(object, Short.parseShort(fieldValue));
            }
            if (ClassUtils.isAssignable(clazz, Character.class)) {
                field.set(object, fieldValue.charAt(0));
            }
            if (ClassUtils.isAssignable(clazz, Double.class)) {
                field.set(object, Double.parseDouble(fieldValue));
            }
            if (ClassUtils.isAssignable(clazz, Float.class)) {
                field.set(object, Float.parseFloat(fieldValue));
            }
            if (ClassUtils.isAssignable(clazz, Long.class)) {
                field.set(object, Long.parseLong(fieldValue));
            }
            if (ClassUtils.isAssignable(clazz, Boolean.class)) {
                field.set(object, Boolean.parseBoolean(fieldValue));
            }
        } else if (ClassUtils.isAssignable(String.class, clazz)) {
            field.set(object, fieldValue);
        } else {
            Object obj = MAPPER.readValue(fieldValue, clazz);
            field.set(object, obj);
        }
    }

    public static String getString(Field field, Object object) throws Exception {
        Object obj = get(field, object);
        if (null != obj) {
            Class clazz = obj.getClass();
            if (ClassUtils.isPrimitiveOrWrapper(clazz) || ClassUtils.isAssignable(String.class, clazz)) {
                return obj.toString();
            } else {
                return MAPPER.writeValueAsString(field.get(object));
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    public static Object get(Field field, Object object) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }
}
