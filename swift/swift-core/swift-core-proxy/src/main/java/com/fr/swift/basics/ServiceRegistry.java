package com.fr.swift.basics;

/**
 * @author yee
 * @date 2018/10/30
 */
public interface ServiceRegistry {
    void registerService(Object service);

    Object getInternalService(String serviceClass);

    Object getExternalService(String serviceClass);

    Object getService(String proxyClass);
}
