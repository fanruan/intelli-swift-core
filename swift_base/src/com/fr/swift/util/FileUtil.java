package com.fr.swift.util;

import com.fr.stable.CommonUtils;

import java.io.File;

/**
 * @author anchore
 * @date 2018/1/4
 */
public class FileUtil {
    /**
     * @param f 文件或目录
     */
    public static boolean delete(File f) {
        return CommonUtils.deleteFile(f);
    }

    public static boolean delete(String path) {
        return delete(new File(path));
    }
}
