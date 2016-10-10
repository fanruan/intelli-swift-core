package com.fr.bi.cal.analyze.executor.utils;

/**
 * Created by Young's on 2016/9/21.
 */
public class ExecutorUtils {
    static final String NONE_VALUE = "--";

    public static Object formatExtremeSumValue(Object value) {
        if (value == null) {
            value = NONE_VALUE;
        } else if (value instanceof Double) {
            if (Double.isInfinite((Double) value)) {
                value = "N/0";
            } else if (Double.isNaN((Double) value)) {
                value = "NAN";
            }
        }
        return value;
    }
}
