package com.fr.swift.basics;

import com.fr.swift.basic.URL;

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
     * 拿某个节点上的service代理
     *
     * @param url          节点url
     * @param serviceClass service类
     * @param proxyIface   proxy类接口
     * @param <T>          type of proxy
     * @return ServiceContext的proxy
     */
    <T> T getPeerProxy(URL url, Class<?> serviceClass, Class<T> proxyIface);
}
