package com.fr.swift.basics;

/**
 * @author yee
 * @date 2018/10/30
 */
public interface ServiceRegistry {
    void registerService(Object service);

    <Service> Service getInternalService(Class<Service> serviceClass);

    <Service> Service getExternalService(Class<Service> serviceClass);

    <Service> Service getService(Class<Service> proxyClass);

    Object getService(String proxyClass);
}
