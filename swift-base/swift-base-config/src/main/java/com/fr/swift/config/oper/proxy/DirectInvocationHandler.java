package com.fr.swift.config.oper.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018-11-28
 */
class DirectInvocationHandler implements InvocationHandler {
    private Object object;
    private Class proxyClass;

    DirectInvocationHandler(Object object) {
        this.object = object;
        this.proxyClass = object.getClass();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method invokeMethod = proxyClass.getMethod(method.getName(), method.getParameterTypes());
        return invokeMethod.invoke(object, args);
    }


}
