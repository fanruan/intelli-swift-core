package com.fr.swift.decision.config.base;

import java.util.Map;

/**
 * @author yee
 * @date 2018/6/15
 */
public interface SwiftMapConfig<T> extends SwiftConfig {
    boolean addOrUpdate(String key, T value);

    T get(String key);

    Map<String, T> get();

    boolean remove(String key);
}
