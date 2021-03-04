package com.fr.swift.cloud.util;

import com.fr.swift.cloud.base.json.JsonBuilder;
import com.fr.swift.cloud.log.SwiftLoggers;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author yee
 * @date 2018/7/16
 */
public final class ReflectUtils {

    public static Object parseObject(Class tClass, String fieldValue) {
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
        } else if (tClass.isEnum()) {
            return Enum.valueOf(tClass, fieldValue);
        } else if (ClassUtils.isAssignable(Map.class, tClass)) {
            try {
                return JsonBuilder.readValue(fieldValue, tClass);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("error json line: {}", fieldValue);
            }
            return Collections.emptyMap();
        }
        return null;
    }

    public static Number parseNumber(Object o) {
        if (null == o) {
            return 0;
        }
        Class clazz = o.getClass();
        if (ClassUtils.isPrimitiveOrWrapper(clazz)) {
            if (ClassUtils.isAssignable(clazz, Integer.class)) {
                return (Integer) o;
            }
            if (ClassUtils.isAssignable(clazz, Byte.class)) {
                return (Byte) o;
            }
            if (ClassUtils.isAssignable(clazz, Short.class)) {
                return (Short) o;
            }
            if (ClassUtils.isAssignable(clazz, Character.class)) {
                char charValue = ((Character) o).charValue();
                if (charValue >= '0' && charValue <= '9') {
                    return Integer.parseInt(o.toString());
                }
                return (int) charValue;
            }
            if (ClassUtils.isAssignable(clazz, Double.class)) {
                return (Double) o;
            }
            if (ClassUtils.isAssignable(clazz, Float.class)) {
                return (Float) o;
            }
            if (ClassUtils.isAssignable(clazz, Long.class)) {
                return (Long) o;
            }
            if (ClassUtils.isAssignable(clazz, Boolean.class)) {
                return (Boolean) o ? 1 : 0;
            }
        } else if (o instanceof String) {
            Date date = DateUtils.string2Date((String) o);
            if (null != date) {
                return date.getTime();
            }
            return new BigDecimal((String) o);
        } else if (o instanceof Date) {
            return ((Date) o).getTime();
        } else {
            return 0;
        }
        return 0;
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) throws Exception {
        return ConstructorUtils.invokeConstructor(clazz, args);
    }


    public static String getString(Field field, Object object) throws Exception {
        Object obj = FieldUtils.readField(field, object, true);
        if (null != obj) {
            Class clazz = obj.getClass();
            if (clazz.isEnum()) {
                return ((Enum) obj).name();
            }
            return obj.toString();
        } else {
            return Strings.EMPTY;
        }
    }


    public static <T> T newInstance(Class<T> c) throws IllegalAccessException, InstantiationException {
        return c.newInstance();
    }

    public static <T> Optional<T> newInstance(String className, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            return Optional.ofNullable((T) newInstance(clazz, args));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
