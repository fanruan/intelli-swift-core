package com.fr.swift.cloud.util;

/**
 * @author anchore
 *
 * @deprecated 没啥太大作用，跟风BI抄过来的，一般的throw也能做得很好
 */
@Deprecated
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
