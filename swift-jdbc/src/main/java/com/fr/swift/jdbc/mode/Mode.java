package com.fr.swift.jdbc.mode;

/**
 * @author yee
 * @date 2018/8/29
 */
public enum Mode {
    EMB, SERVER;

    public static Mode fromKey(String mode) {
        return Mode.valueOf(mode.toUpperCase());
    }
}
