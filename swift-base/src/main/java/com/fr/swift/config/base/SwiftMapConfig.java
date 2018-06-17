package com.fr.swift.config.base;

/**
 * @author yee
 * @date 2018/6/15
 */
public interface SwiftMapConfig<T> extends SwiftConfig {
    boolean addOrUpdate(String key, T value);

    T get(String key);

    boolean remove(String key);
}
