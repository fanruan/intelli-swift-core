package com.fr.swift.basics;

import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/23
 */
public interface InvokerHandler {
    /**
     * invoke method
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
