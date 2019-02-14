package com.fr.swift.basics;

import com.fr.swift.basic.URL;

import java.util.Map;

/**
 * @author yee
 * @date 2018/10/23
 */
public interface ProxyFactory {

    /**
     * 根据需要代理的类获取代理对象
     * 一般调到了所有节点上的service
     *
     * @param proxy proxy class
     * @param <T>   class
     * @return class instance
     */
    <T> T getProxy(Class<T> proxy);

    /**
     * 拿各个节点上的service代理
     *
     * @param proxyClass 代理类
     * @param <T>        class
     * @return url -> proxy
     */
    <T> Map<URL, T> getPeerProxies(Class<T> proxyClass);
}
