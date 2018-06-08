package com.fr.swift.test;

/**
 * @author anchore
 * @date 2018/6/8
 */
public class TestResource {
    public static String getRunPath(Class<?> c) {
        return System.getProperty("user.dir") + "/test_temp/" + c.getSimpleName();
    }

    public static String getCallerClassName() {
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        return traces[traces.length - 1].getClassName();
    }

    public static String getCallerClassSimpleName() {
        return getCallerClassName().substring(getCallerClassName().lastIndexOf(".") + 1);
    }
}