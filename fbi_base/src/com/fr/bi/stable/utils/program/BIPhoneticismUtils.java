package com.fr.bi.stable.utils.program;

import com.fr.bi.stable.structure.collection.map.lru.LRUHashMap;
import com.fr.stable.pinyin.PinyinHelper;

/**
 * 音标操作
 * Created by GUY on 2015/3/31.
 */
public class BIPhoneticismUtils {
    private static int CACHE_SIZE = 2048;
    private static LRUHashMap<String, String> pyMap = new LRUHashMap<String, String>(CACHE_SIZE);

    public static String getPingYin(String name) {
        String py = name;
        if (!pyMap.containsKey(name)) {
            String p = PinyinHelper.getShortPinyin(name);
            if(p != null){
                py = p;
            }
            pyMap.put(name, py);
        }else{
            py = pyMap.get(name);
        }
        return py;
    }
}