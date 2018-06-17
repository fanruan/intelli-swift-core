package com.fr.swift.test;

/**
 * @author anchore
 * @date 2018/6/8
 */
public class TestResource {
    public static String getRunPath() {
        return String.format("%s/test_temp/%s",
                System.getProperty("user.dir"),
                getClassSimpleName(Thread.currentThread().getStackTrace()[2].getClassName()));
    }

    public static String getRunPath(Class<?> c) {
        return System.getProperty("user.dir") + "/test_temp/" + c.getSimpleName();
    }

    public static String getCallerClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    private static String getClassSimpleName(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }
}