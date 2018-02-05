package com.fr.swift.util;

/**
 * @author anchore
 */
public class Crasher {
    public static <T> T crash(String msg, Throwable t) {
        throw new RuntimeException(msg, t);
    }

    public static <T> T crash(String msg) {
        throw new RuntimeException(msg, null);
    }

    public static <T> T crash(Throwable t) {
        throw new RuntimeException(null, t);
    }
}
