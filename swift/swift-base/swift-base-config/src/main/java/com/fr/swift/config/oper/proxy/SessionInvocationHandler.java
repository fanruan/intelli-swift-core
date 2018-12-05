package com.fr.swift.config.oper.proxy;

import com.fr.swift.config.oper.ConfigCriteria;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigTransaction;
import com.fr.swift.config.oper.exception.SwiftEntityExistsException;
import com.fr.swift.config.oper.impl.VersionConfigProperty;
import com.fr.swift.util.ReflectUtils;

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

    public SessionInvocationHandler(Object object) {
        this.object = object;
        this.proxyClass = object.getClass();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method invokeMethod = proxyClass.getMethod(method.getName(), method.getParameterTypes());
        try {
            Object obj = invokeMethod.invoke(object, args);
            if (method.getName().equals("createCriteria")) {
                return Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[]{ConfigCriteria.class}, new CriteriaInvocationHandler(obj));
            }
            if (method.getName().equals("createQuery")) {
                return Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[]{ConfigQuery.class}, new DirectInvocationHandler(obj));
            }

            if (method.getName().equals("beginTransaction")) {
                return Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[]{ConfigTransaction.class}, new DirectInvocationHandler(obj));
            }
            return obj;
        } catch (Throwable throwable) {
            throwThrowable(throwable);
            throw throwable;
        }
    }

    private void throwThrowable(Throwable throwable) {
        if (ReflectUtils.isAssignable(throwable.getClass(), VersionConfigProperty.get().getEntityExistsException())) {
            throw new SwiftEntityExistsException(throwable);
        }
        if (null != throwable.getCause()) {
            throwThrowable(throwable.getCause());
        } else {
            return;
        }
    }
}
