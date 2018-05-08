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

    public static <T> T crash() {
        throw new RuntimeException(null, null);
    }

    public static <T> T checkedCrash(String msg, Throwable t) throws Exception {
        throw new Exception(msg, t);
    }

    public static <T> T checkedCrash(String msg) throws Exception {
        throw new Exception(msg, null);
    }

    public static <T> T checkedCrash(Throwable t) throws Exception {
        throw new Exception(null, t);
    }

    public static <T> T checkedCrash() throws Exception {
        throw new Exception(null, null);
    }
}
