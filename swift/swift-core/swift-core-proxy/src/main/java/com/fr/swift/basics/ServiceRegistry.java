package com.fr.swift.basics;

/**
 * @author yee
 * @date 2018/10/30
 */
public interface ServiceRegistry {
    void registerService(Object service);

    Object getInternalService(String serviceClass);

    <Service> Service getInternalService(Class<Service> serviceClass);

    Object getExternalService(String serviceClass);

    <Service> Service getExternalService(Class<Service> serviceClass);

    Object getService(String proxyClass);
}
