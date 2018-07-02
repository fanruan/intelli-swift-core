package com.fr.swift.context;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class ContextUtil {
    public static String getClassPath() {
        return ContextUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
}