package com.fr.swift.util;

/**
 * @author anchore
 */
public class Crasher {
    public static <T> T crash(String msg, Throwable t) {
        throw new CrashException(msg, t);
    }

    public static <T> T crash(String msg) {
        throw new CrashException(msg);
    }

    public static <T> T crash(Throwable t) {
        throw new CrashException(t);
    }

    public static <T> T crash() {
        throw new CrashException();
    }

    public static class CrashException extends RuntimeException {
        CrashException() {
        }

        CrashException(String message) {
            super(message);
        }

        CrashException(String message, Throwable cause) {
            super(message, cause);
        }

        CrashException(Throwable cause) {
            super(cause);
        }
    }
}
