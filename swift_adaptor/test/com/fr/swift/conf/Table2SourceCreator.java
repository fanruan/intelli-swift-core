package com.fr.swift.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/14
 */
public class Table2SourceCreator {
    public static Map<String, String> create(int size) {
        Map<String, String> result = new HashMap<String, String>(size);
        for (int i = 0; i < size; i++) {
            result.put("table" + i, "source" + i);
        }
        return result;
    }

    public static Map<String, String> modify(int size) {
        Map<String, String> result = new HashMap<String, String>(size);
        for (int i = 0; i < size; i++) {
            result.put("table" + i, "modifySource" + i);
        }
        return result;
    }
}
