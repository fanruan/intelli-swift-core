package com.fr.swift.util;

import com.fr.swift.base.json.JsonBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yee
 * @date 2018/7/16
 */
public final class ReflectUtils {

    //    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final static Map<Class, Class> primitiveWrapperMap = new HashMap<Class, Class>();
    private final static Map<Class, Class> wrapperPrimitiveMap;

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
        wrapperPrimitiveMap = new HashMap();

        Iterator var0 = primitiveWrapperMap.keySet().iterator();

        while (var0.hasNext()) {
            Class<?> primitiveClass = (Class) var0.next();
            Class<?> wrapperClass = (Class) primitiveWrapperMap.get(primitiveClass);
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    public static void set(Field field, Object object, String fieldValue) throws Exception {
        field.setAccessible(true);
        Class clazz = field.getType();
        field.set(object, parseObject(clazz, fieldValue));
    }

    public static Object parseObject(Class tClass, String fieldValue) throws Exception {
        if (isPrimitiveOrWrapper(tClass)) {
            Class clazz = isPrimitiveWrapper(tClass) ? tClass : primitiveToWrapper(tClass);
            if (isAssignable(clazz, Integer.class)) {
                return Integer.parseInt(fieldValue);
            }
            if (isAssignable(clazz, Byte.class)) {
                return Byte.parseByte(fieldValue);
            }
            if (isAssignable(clazz, Short.class)) {
                return Short.parseShort(fieldValue);
            }
            if (isAssignable(clazz, Character.class)) {
                return fieldValue.charAt(0);
            }
            if (isAssignable(clazz, Double.class)) {
                return Double.parseDouble(fieldValue);
            }
            if (isAssignable(clazz, Float.class)) {
                return Float.parseFloat(fieldValue);
            }
            if (isAssignable(clazz, Long.class)) {
                return Long.parseLong(fieldValue);
            }
            if (isAssignable(clazz, Boolean.class)) {
                return Boolean.parseBoolean(fieldValue);
            }
        } else if (isAssignable(String.class, tClass)) {
            return fieldValue;
        } else if (tClass.isEnum()) {
            return Enum.valueOf(tClass, fieldValue);
//            Object obj = MAPPER.readValue(fieldValue, tClass);
//            return obj;
        } else {
            return JsonBuilder.readValue(fieldValue, tClass);
        }
        return null;
    }

    public static Number parseNumber(Object o) {
        if (null == o) {
            return 0;
        }
        Class clazz = o.getClass();
        if (isPrimitiveOrWrapper(clazz)) {
            if (isAssignable(clazz, Integer.class)) {
                return (Integer) o;
            }
            if (isAssignable(clazz, Byte.class)) {
                return (Byte) o;
            }
            if (isAssignable(clazz, Short.class)) {
                return (Short) o;
            }
            if (isAssignable(clazz, Character.class)) {
                char charValue = ((Character) o).charValue();
                if (charValue >= '0' && charValue <= '9') {
                    return Integer.parseInt(o.toString());
                }
                return (int) charValue;
            }
            if (isAssignable(clazz, Double.class)) {
                return (Double) o;
            }
            if (isAssignable(clazz, Float.class)) {
                return (Float) o;
            }
            if (isAssignable(clazz, Long.class)) {
                return (Long) o;
            }
            if (isAssignable(clazz, Boolean.class)) {
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
        return getConstructor(clazz, args).newInstance(args);
    }

    private static <T> Constructor<T> getConstructor(Class<T> clazz, Object... args) throws
            NoSuchMethodException {
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
                    if (null != args[i] && !isAssignable(classes[i], args[i].getClass())) {
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
            if (isPrimitiveOrWrapper(clazz) || isAssignable(String.class, clazz)) {
                return obj.toString();
            } else if (clazz.isEnum()) {
                return ((Enum) obj).name();
            } else {
                return JsonBuilder.writeJsonString(obj);
            }
        } else {
            return Strings.EMPTY;
        }
    }

    public static Object get(Field field, Object object) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        if (type == null) {
            return false;
        } else {
            return type.isPrimitive() || isPrimitiveWrapper(type);
        }
    }

    public static boolean isPrimitiveWrapper(Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }

    public static Class<?> primitiveToWrapper(Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = (Class) primitiveWrapperMap.get(cls);
        }

        return convertedClass;
    }

    public static Class<?> wrapperToPrimitive(Class<?> cls) {
        return (Class) wrapperPrimitiveMap.get(cls);
    }

    public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
        if (toClass == null) {
            return false;
        } else if (cls == null) {
            return !toClass.isPrimitive();
        } else {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }

            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }

            if (cls.equals(toClass)) {
                return true;
            } else if (cls.isPrimitive()) {
                if (!toClass.isPrimitive()) {
                    return false;
                } else if (Integer.TYPE.equals(cls)) {
                    return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
                } else if (Long.TYPE.equals(cls)) {
                    return Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
                } else if (Boolean.TYPE.equals(cls)) {
                    return false;
                } else if (Double.TYPE.equals(cls)) {
                    return false;
                } else if (Float.TYPE.equals(cls)) {
                    return Double.TYPE.equals(toClass);
                } else if (Character.TYPE.equals(cls)) {
                    return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
                } else if (Short.TYPE.equals(cls)) {
                    return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
                } else if (!Byte.TYPE.equals(cls)) {
                    return false;
                } else {
                    return Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
                }
            } else {
                return toClass.isAssignableFrom(cls);
            }
        }
    }

    public static <T> T newInstance(Class<T> c) throws IllegalAccessException, InstantiationException {
        return c.newInstance();
    }

    public static <T> Optional<T> newInstance(String className, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            return Optional.of((T) newInstance(clazz, args));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
