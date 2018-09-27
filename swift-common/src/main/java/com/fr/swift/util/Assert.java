package com.fr.swift.util;

/**
 * @author anchore
 * @date 2018/7/11
 */
public class Assert extends com.fr.third.springframework.util.Assert {
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }
}