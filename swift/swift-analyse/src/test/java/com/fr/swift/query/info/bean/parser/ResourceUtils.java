package com.fr.swift.query.info.bean.parser;

import com.fr.swift.util.Crasher;

import java.io.File;

/**
 * Created by pony on 2017/12/29.
 */
// TODO: 2018/11/29 重复
public class ResourceUtils {
    public static String getFileAbsolutePath(String path) {
        String absPath = null;
        try {
            absPath = new File(ResourceUtils.class.getClassLoader().getResource(path).toURI()).getAbsolutePath();
        } catch (Exception e) {
            Crasher.crash(e);
        }
        return absPath;
    }
}
