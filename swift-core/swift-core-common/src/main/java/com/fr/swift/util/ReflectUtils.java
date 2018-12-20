package com.fr.swift.util;

import com.fr.stable.StringUtils;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Constructor;
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
        field.set(object, parseObject(clazz, fieldValue));
    }

    public static Object parseObject(Class tClass, String fieldValue) throws Exception {
        if (ClassUtils.isPrimitiveOrWrapper(tClass)) {
            Class clazz = ClassUtils.isPrimitiveWrapper(tClass) ? tClass : ClassUtils.primitiveToWrapper(tClass);
            if (ClassUtils.isAssignable(clazz, Integer.class)) {
                return Integer.parseInt(fieldValue);
            }
            if (ClassUtils.isAssignable(clazz, Byte.class)) {
                return Byte.parseByte(fieldValue);
            }
            if (ClassUtils.isAssignable(clazz, Short.class)) {
                return Short.parseShort(fieldValue);
            }
            if (ClassUtils.isAssignable(clazz, Character.class)) {
                return fieldValue.charAt(0);
            }
            if (ClassUtils.isAssignable(clazz, Double.class)) {
                return Double.parseDouble(fieldValue);
            }
            if (ClassUtils.isAssignable(clazz, Float.class)) {
                return Float.parseFloat(fieldValue);
            }
            if (ClassUtils.isAssignable(clazz, Long.class)) {
                return Long.parseLong(fieldValue);
            }
            if (ClassUtils.isAssignable(clazz, Boolean.class)) {
                return Boolean.parseBoolean(fieldValue);
            }
        } else if (ClassUtils.isAssignable(String.class, tClass)) {
            return fieldValue;
        } else if (ClassUtils.isAssignable(tClass, Enum.class)) {
            return Enum.valueOf(tClass, fieldValue);
        } else {
            Object obj = MAPPER.readValue(fieldValue, tClass);
            return obj;
        }
        return null;
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) throws Exception {
        return getConstructor(clazz, args).newInstance(args);
    }

    private static <T> Constructor<T> getConstructor(Class<T> clazz, Object... args) throws NoSuchMethodException {
        if (null == args || args.length == 0) {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor;
        }
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        for (Constructor<T> constructor : constructors) {
            Class[] classes = constructor.getParameterTypes();
            if (classes.length == args.length) {
                boolean marked = true;
                for (int i = 0; i < classes.length; i++) {
                    if (null != args[i] && !ClassUtils.isAssignable(classes[i], args[i].getClass())) {
                        marked = false;
                        break;
                    }
                }
                if (marked) {
                    constructor.setAccessible(true);
                    return constructor;
                }
            }
        }
        throw new NoSuchMethodException();
    }

    public static String getString(Field field, Object object) throws Exception {
        Object obj = get(field, object);
        if (null != obj) {
            Class clazz = obj.getClass();
            if (ClassUtils.isPrimitiveOrWrapper(clazz) || ClassUtils.isAssignable(String.class, clazz)) {
                return obj.toString();
            } else if (obj instanceof Enum) {
                return ((Enum) obj).name();
            } else {
                return MAPPER.writeValueAsString(obj);
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
