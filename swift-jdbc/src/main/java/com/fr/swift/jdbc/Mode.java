package com.fr.swift.jdbc;

/**
 * @author yee
 * @date 2018/11/16
 */
public enum Mode {
    //
    EMB, REMOTE;
    private static final String OLD_REMOTE = "SERVER";

    public static Mode fromKey(String key) {
        String testKey = key.trim().toUpperCase();
        if (OLD_REMOTE.equals(testKey)) {
            return REMOTE;
        }
        return valueOf(testKey);
    }
}
