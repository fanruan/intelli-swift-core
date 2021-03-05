package com.fr.swift.cloud.config;

import java.util.Collection;

/**
 * @author yee
 * @date 2019-04-26
 */
public interface SwiftConfigRegistry {
    Collection<Class<?>> getEntities();

    void registerEntity(String clazzName, ClassLoader loader) throws ClassNotFoundException;

    void registerEntity(String clazzName) throws ClassNotFoundException;
}
