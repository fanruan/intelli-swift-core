package com.fr.swift.util;

/**
 * @author Hoky
 * @date 2020/11/3
 */
public class SegmentInfoUtils {
    public static String getTemDir(String segUrl) {
        return segUrl == null ? "0" : segUrl.substring(segUrl.lastIndexOf("/") + 1);
    }
}