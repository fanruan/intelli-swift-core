package com.fr.swift.config.oper.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018-11-28
 */
class DirectInvocationHandler implements InvocationHandler {
    private Object object;

    DirectInvocationHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(object, args);
    }


}
