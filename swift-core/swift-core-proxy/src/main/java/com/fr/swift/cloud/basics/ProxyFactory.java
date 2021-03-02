package com.fr.swift.cloud.basics;

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

}