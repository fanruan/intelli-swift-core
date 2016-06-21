package com.fr.bi.stable.utils.program;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class BIFieldUtils {
    public BIFieldUtils() {
    }

    public static Boolean isBasicValue(Field field) {
        return BITypeUtils.isBasicValue(field.getType());
    }

    public static Boolean isIterableObject(Object fieldValue) {
        return fieldValue instanceof Iterable;
    }

    public static Boolean isMapObject(Object fieldValue) {
        return fieldValue instanceof Map;
    }

    public static Boolean isArrayObject(Object fieldValue) {
        return fieldValue.getClass().isArray();
    }

    public static Boolean isBasicType(Class fieldClass) {
        return BITypeUtils.isBasicValue(fieldClass);
    }

    public static Boolean isIterableType(Class fieldClass) {
        return BITypeUtils.isAssignable(Iterable.class, fieldClass);
    }
  
    public static Boolean isCollectionType(Class fieldClass) {
        return BITypeUtils.isAssignable(Collection.class, fieldClass);
    }

    public static Boolean isMapType(Class fieldClass) {
        return BITypeUtils.isAssignable(Map.class, fieldClass);
    }


    public static Boolean isArrayType(Class fieldClass) {
        return fieldClass.isArray();
    }
}