package com.fr.swift.test;

/**
 * @author anchore
 * @date 2018/6/8
 */
public class TestResource {
    public static String getRunPath(Class<?> c) {
        return getTmpDir() + "/" + c.getSimpleName();
    }

    public static String getTmpDir() {
        return System.getProperty("user.dir") + "/test_temp";
    }
}