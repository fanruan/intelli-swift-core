package com.fr.swift.util.pinyin;

import com.fr.stable.pinyin.PinyinHelper;
import com.fr.swift.structure.lru.LRUHashMap;

/**
 * 音标操作
 * Created by GUY on 2015/3/31.
 */
public class BIPinyinUtils {

    private static int CACHE_SIZE = 2048;
    private static LRUHashMap<String, String> pyMap = new LRUHashMap<String, String>(CACHE_SIZE);

    private static String getPingYin(String name) {
        String py = name;
        if (!pyMap.containsKey(name)) {
            String p = PinyinHelper.getShortPinyin(name);
            if(p != null) {
                py = p;
            }
            pyMap.put(name, py);
        }else{
            py = pyMap.get(name);
        }
        return py;
    }

    /**
     * 字符串（忽略大小写）是否包含 || 中文拼音首字母字符串是否包含
     * @param keyword 搜索关键字
     * @param value 比较的值
     * @return
     */
    public static boolean isMatch(String keyword, String value) {

        String py = getPingYin(value);
        if (keyword == null || value.toUpperCase().contains(keyword.toUpperCase())
                || py.toUpperCase().contains(keyword.toUpperCase())) {
            return true;
        }
        return false;
    }
}