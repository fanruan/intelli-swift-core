package com.fr.swift.basics;

/**
 * @author yee
 * @date 2018/10/23
 */
public interface ProxyFactory {

    /**
     * 根据需要代理的类获取代理对象
     *
     * @param proxy
     * @param <T>
     * @return
     */
    <T> T getProxy(Class<T> proxy);

    /**
     * 获取ProcessHandlerRegistry
     *
     * @return
     */
    ProcessHandlerRegistry getRegistry();
}
