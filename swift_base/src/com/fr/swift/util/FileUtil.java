package com.fr.swift.util;

import java.io.File;

/**
 * @author anchore
 * @date 2018/1/4
 */
public class FileUtil {
    /**
     * CommonUtils.deleteFile删文件有问题。。。
     * @param f 文件或目录
     */
    public static void delete(File f) {
        if (f == null || !f.exists()) {
            return;
        }

        if (f.isDirectory()) {
            File[] children = f.listFiles();

            if (children == null) {
                f.delete();
                return;
            }

            for (File child : children) {
                delete(child);
            }
        }

        f.delete();
    }

    public static void delete(String path) {
        delete(new File(path));
    }
}
