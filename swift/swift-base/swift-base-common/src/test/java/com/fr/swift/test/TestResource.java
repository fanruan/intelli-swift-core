package com.fr.swift.test;

/**
 * @author anchore
 * @date 2018/6/8
 */
public class TestResource {
    public static String getRunPath(Class<?> c) {
        return getTestDir() + "/" + c.getSimpleName();
    }

    public static String getTestDir() {
        return System.getProperty("test.dir", System.getProperty("user.dir")) + "/test_temp";
    }
}