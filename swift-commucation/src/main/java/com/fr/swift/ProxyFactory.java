package com.fr.swift;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ProxyFactory {

    <T> T getProxy(Invoker<T> invoker) throws Exception;

    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws Exception;

}
