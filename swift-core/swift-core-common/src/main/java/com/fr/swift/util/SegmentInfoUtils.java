package com.fr.swift.util;

/**
 * @author Hoky
 * @date 2020/11/3
 */
public class SegmentInfoUtils {
    public static int getYearMonthFromOrder(int order) {
        return order / 100;
    }

    public static String getTemDir(String segUrl) {
        return segUrl.substring(segUrl.lastIndexOf("/"));
    }
}
