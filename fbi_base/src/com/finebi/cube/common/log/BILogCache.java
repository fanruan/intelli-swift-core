package com.finebi.cube.common.log;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wang on 2017/8/29.
 */
public class BILogCache {
    private LoadingCache<String, String> cache;

    private static BILogCache instance = new BILogCache();

    public static BILogCache getInstance() {
        return instance;
    }
    private static int MAX_SIZE = 1000;
    private static int EXPIRE_TIME = 1;
    public BILogCache() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(MAX_SIZE)
                .expireAfterAccess(EXPIRE_TIME, TimeUnit.SECONDS)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return StringUtils.EMPTY;
                    }
                });
    }

    public boolean containsKey(String key) {
        try {
            if (key == null) {
                return false;
            }
            if (ComparatorUtils.equals(cache.get(key), StringUtils.EMPTY)) {
                cache.put(key, StringUtils.BLANK);
                return false;
            } else {
                return true;
            }
        } catch (ExecutionException ignore) {
            return false;
        }
    }
}
