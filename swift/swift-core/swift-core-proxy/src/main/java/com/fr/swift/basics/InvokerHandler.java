package com.fr.swift.basics;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/23
 */
public interface InvokerHandler extends InvocationHandler {
    /**
     * invoke method
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
