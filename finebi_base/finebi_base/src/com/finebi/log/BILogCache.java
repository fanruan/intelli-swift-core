package com.finebi.log;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;

import java.util.HashMap;

/**
 * Created by wang on 2017/8/29.
 */
public class BILogCache {
    private static final int MAX_SIZE = 1000;
    private HashMap<String, String> cache;
    private static BILogCache instance = new BILogCache();

    public static BILogCache getInstance() {
        return instance;
    }

    public BILogCache() {
        cache = new HashMap<String, String>(MAX_SIZE);
    }

    public boolean containsKey(String key) {
        if (key == null) {
            return false;
        }
        if (ComparatorUtils.equals(cache.get(key), StringUtils.EMPTY)) {
            cache.put(key, StringUtils.BLANK);
            return false;
        } else {
            return true;
        }
    }
}
