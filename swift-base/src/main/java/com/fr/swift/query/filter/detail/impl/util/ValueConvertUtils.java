package com.fr.swift.query.filter.detail.impl.util;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/28.
 */
public class ValueConvertUtils {

    public static Object convert(Object value, DictionaryEncodedColumn.Type type) {
        if (value == null) {
            return null;
        }
        switch (type) {
            case LONG:
                return ((Number) value).longValue();
            case DOUBLE:
                return ((Number) value).doubleValue();
            case INT:
                return ((Number) value).intValue();
            default:
                return value.toString();
        }
    }

    public static List convert(Set values, DictionaryEncodedColumn.Type type) {
        List list = new ArrayList();
        for (Object object : values) {
            list.add(convert(object, type));
        }
        return list;
    }
}
