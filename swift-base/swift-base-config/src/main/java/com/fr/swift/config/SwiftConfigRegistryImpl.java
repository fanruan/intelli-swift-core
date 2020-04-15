package com.fr.swift.config;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yee
 * @date 2019-04-26
 */
public enum SwiftConfigRegistryImpl implements SwiftConfigRegistry {
    /**
     *
     */
    INSTANCE;

    private ConcurrentMap<String, Class<?>> dynamicEntities = new ConcurrentHashMap<String, Class<?>>();

    @Override
    public Collection<Class<?>> getEntities() {
        return dynamicEntities.values();
    }

    @Override
    public void registerEntity(String className, ClassLoader loader) throws ClassNotFoundException {
        dynamicEntities.putIfAbsent(className, loader.loadClass(className));
    }

    @Override
    public void registerEntity(String clazzName) throws ClassNotFoundException {
        registerEntity(clazzName, ClassLoader.getSystemClassLoader());
    }
}
