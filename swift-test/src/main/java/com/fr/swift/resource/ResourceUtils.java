package com.fr.swift.resource;

import java.io.File;

/**
 * Created by pony on 2017/12/29.
 */
public class ResourceUtils {
    public static String getFileAbsolutePath(String path){
        String absPath = ResourceUtils.class.getClassLoader().getResource(path).getFile();
        if (absPath.startsWith(File.separator) || absPath.startsWith("/")){
            absPath = absPath.substring(1, absPath.length());
        }
        return absPath;
    }
}
