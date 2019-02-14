package com.fr.swift.source.excel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connery on 2015/12/4.
 */
public class ExcelFileLockUtils {

    private static Map<String, Object> fileLockMap = new ConcurrentHashMap<String, Object>();

    public static Object getImageLock(String filePath) {
        Object lock = null;
        synchronized (fileLockMap) {
            lock = fileLockMap.get(filePath);
            if (lock == null) {
                lock = new Object();
                fileLockMap.put(filePath, lock);
            }
        }
        return lock;
    }

}