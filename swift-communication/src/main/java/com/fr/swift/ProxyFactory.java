package com.fr.swift;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ProxyFactory {

    <T> T getProxy(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url);

    <T> T getProxy(T proxy, Class<T> type, URL url);

    /**
     * @param proxy
     * @param type
     * @param url
     * @param sync  同步异步执行标志
     * @param <T>
     * @return
     */
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url, boolean sync);
}
