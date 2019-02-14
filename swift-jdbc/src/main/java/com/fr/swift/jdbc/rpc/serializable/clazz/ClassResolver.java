package com.fr.swift.jdbc.rpc.serializable.clazz;

/**
 * @author yee
 * @date 2019-01-11
 */
public interface ClassResolver {
    Class<?> resolve(String className) throws ClassNotFoundException;
}
