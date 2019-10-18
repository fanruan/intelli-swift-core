package com.fr.swift.segment.operator.utils;

import com.fr.swift.util.Strings;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class InserterUtils {

    public static <V> boolean isBusinessNullValue(V val) {
        if (val == null) {
            return true;
        }
        // string的空白串也视为空值
        return val instanceof String && Strings.isBlank((String) val);
    }
}
