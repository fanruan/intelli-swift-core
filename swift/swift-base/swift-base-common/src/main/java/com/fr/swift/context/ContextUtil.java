package com.fr.swift.context;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class ContextUtil {
    public static String getClassPath() {
        return getClassPath(ContextUtil.class);
    }

    public static String getClassPath(Class<?> c) {
        String path = c.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            return URLDecoder.decode(path, "utf8");
        } catch (UnsupportedEncodingException e) {
            return path;
        }
    }

//    public static String getContextPath() {
//        String frPath = WorkContext.getCurrent().getPath();
//        if (frPath != null) {
//            return frPath + "/../";
//        }
//        String classPath = getClassPath();
//        return new File(classPath).isDirectory() ? classPath + "/../" : classPath + "/../../";
//    }
}