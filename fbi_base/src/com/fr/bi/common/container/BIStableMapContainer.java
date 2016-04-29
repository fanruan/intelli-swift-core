package com.fr.bi.common.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 不会返回null的容器
 * 需要实现构建对象的方法
 * Created by Connery on 2015/12/7.
 */
public abstract class BIStableMapContainer<K, V> extends BIMapContainer<K, V> {
    @Override
    protected Map<K, V> initContainer() {
        return new ConcurrentHashMap<K, V>();
    }

    @Override
    protected V generateAbsentValue(K key) {
        return constructValue(key);
    }

    public abstract V constructValue(K key);
}