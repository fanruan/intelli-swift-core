package com.fr.bi.stable.utils.file;

/**
 * Created by GUY on 2015/3/4.
 */

/**
 * 文件路径生成工具
 */
public class BIPathUtils {

    private final static String SUFFIX = ".fcube";
    private static final String INDEX = "_index";
    private static final String SIZE = "_size";

    public static String createIndexPath(String basePath) {
        return basePath + INDEX + SUFFIX;
    }

    public static String createSizePath(String basePath) {
        return basePath + SIZE + SUFFIX;
    }

}