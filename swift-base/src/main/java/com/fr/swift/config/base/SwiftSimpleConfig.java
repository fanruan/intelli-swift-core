package com.fr.swift.config.base;

/**
 * @author yee
 * @date 2018/6/15
 */
public interface SwiftSimpleConfig<T> extends SwiftConfig {
    boolean addOrUpdate(T obj);

    T get();

    boolean remove();
}
