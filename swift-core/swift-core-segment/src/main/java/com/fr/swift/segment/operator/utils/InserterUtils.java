package com.fr.swift.segment.operator.utils;

import com.fr.stable.StringUtils;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.util.Crasher;

import java.util.Map;

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
        return val instanceof String && StringUtils.isBlank((String) val);
    }

    public static void setNullIndex(String columnName, int row, Map<String, MutableBitMap> nullMap) {
        MutableBitMap nullIndex = nullMap.get(columnName);
        if (null == nullIndex) {
            nullIndex = BitMaps.newRoaringMutable();
        }
        nullIndex.add(row);
        nullMap.put(columnName, nullIndex);
    }

    public static Object getNullValue(ColumnTypeConstants.ClassType clazz) {
        switch (clazz) {
            case INTEGER:
            case LONG:
            case DATE:
                return IOConstant.NULL_LONG;
            case DOUBLE:
                return IOConstant.NULL_DOUBLE;
            case STRING:
                return IOConstant.NULL_STRING;
            default:
                return Crasher.crash("Invalid type: " + clazz);
        }
    }
}
