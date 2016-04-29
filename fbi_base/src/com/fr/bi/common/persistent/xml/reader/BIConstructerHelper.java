package com.fr.bi.common.persistent.xml.reader;

import com.fr.general.ComparatorUtils;

/**
 * Created by Connery on 2016/1/2.
 */
public class BIConstructerHelper {
    public static Object getBoxValue(Class clazz) {
        if (ComparatorUtils.equals(clazz, Integer.class)) {
            return Integer.valueOf(0);
        } else if (ComparatorUtils.equals(clazz, Double.class)) {
            return Double.valueOf(0);
        } else if (ComparatorUtils.equals(clazz, Float.class)) {
            return Float.valueOf(0);
        } else if (ComparatorUtils.equals(clazz, Long.class)) {
            return Long.valueOf(0);
        } else if (ComparatorUtils.equals(clazz, Byte.class)) {
            return Byte.valueOf("0");
        } else if (ComparatorUtils.equals(clazz, Character.class)) {
            return Character.valueOf('d');
        } else if (ComparatorUtils.equals(clazz, Boolean.class)) {
            return Boolean.valueOf(false);
        } else if (ComparatorUtils.equals(clazz, Short.class)) {
            return Short.valueOf("0");
        }
        return null;
    }

    public static Object getPrimitiveValue(Class clazz) {
        if (ComparatorUtils.equals(clazz, int.class)) {
            return Integer.valueOf(0).intValue();
        } else if (ComparatorUtils.equals(clazz, double.class)) {
            return Double.valueOf(0).doubleValue();
        } else if (ComparatorUtils.equals(clazz, float.class)) {
            return Float.valueOf(0).floatValue();
        } else if (ComparatorUtils.equals(clazz, long.class)) {
            return Long.valueOf(0).longValue();
        } else if (ComparatorUtils.equals(clazz, byte.class)) {
            return Byte.valueOf("0").byteValue();
        } else if (ComparatorUtils.equals(clazz, char.class)) {
            return Character.valueOf('d').charValue();
        } else if (ComparatorUtils.equals(clazz, boolean.class)) {
            return Boolean.valueOf(false).booleanValue();
        } else if (ComparatorUtils.equals(clazz, short.class)) {
            return Short.valueOf("0").shortValue();
        }
        return null;
    }
}