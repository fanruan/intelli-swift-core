package com.fr.bi.stable.utils;

/**
 * This class created on 2016/5/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BIParameterUtils {
    public static <T> T pickValue(T parameterValue, T defaultValue) {
        if (parameterValue != null) {
            return parameterValue;
        } else {
            return defaultValue;
        }
    }
}
