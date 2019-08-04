package com.fr.swift.config.oper.proxy;

import com.fr.swift.config.VersionConfigProperty;
import com.fr.swift.config.oper.ConfigTransaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yee
 * @date 2018-11-28
 */
public class SessionInvocationHandler implements InvocationHandler {

    private Object object;
    private Class proxyClass;
    private Class query = null;
    private Class delete = null;

    public SessionInvocationHandler(Object object) throws ClassNotFoundException {
        this.object = object;
        this.proxyClass = object.getClass();
        this.query = Class.forName("com.fr.swift.config.hibernate.HibernateQuery");
        this.delete = Class.forName("com.fr.swift.config.hibernate.HibernateDelete");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("createEntityQuery".equals(method.getName())) {
            Constructor constructor = query.getDeclaredConstructor(Class.class, VersionConfigProperty.get().getSession());
            constructor.setAccessible(true);
            return constructor.newInstance(args[0], object);
        }

        if ("createEntityDelete".equals(method.getName())) {
            Constructor constructor = delete.getDeclaredConstructor(Class.class, VersionConfigProperty.get().getSession());
            constructor.setAccessible(true);
            return constructor.newInstance(args[0], object);
        }
        Method invokeMethod = proxyClass.getMethod(method.getName(), method.getParameterTypes());
        try {
            Object obj = invokeMethod.invoke(object, args);
            if ("beginTransaction".equals(method.getName())) {
                return Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[]{ConfigTransaction.class}, new DirectInvocationHandler(obj));
            }
            return obj;
        } catch (Throwable throwable) {
            throw HibernateThrowableHelper.throwThrowable(throwable, throwable);
        }
    }


}
