package com.fr.swift.query.cache;

/**
 * @author yee
 * @date 2018/6/19
 */
public class Cache<T> {

    private T cacheObject;
    private long createTime;

    public Cache(T cacheObject) {
        this.cacheObject = cacheObject;
        this.createTime = System.currentTimeMillis();
    }

    public long getIdle() {
        return System.currentTimeMillis() - this.createTime;
    }

    public void update() {
        createTime = System.currentTimeMillis();
    }

    public void updateCache(T cacheObj) {
        this.cacheObject = cacheObj;
        update();
    }

    public T get() {
        return this.cacheObject;
    }
}
