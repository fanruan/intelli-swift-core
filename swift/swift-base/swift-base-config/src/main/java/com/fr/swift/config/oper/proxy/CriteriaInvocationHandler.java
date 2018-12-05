package com.fr.swift.config.oper.proxy;

import com.fr.swift.config.oper.exception.SwiftConstraintViolationException;
import com.fr.swift.config.oper.exception.SwiftNonUniqueObjectException;
import com.fr.swift.config.oper.impl.VersionConfigProperty;
import com.fr.swift.util.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018-12-04
 */
public class CriteriaInvocationHandler implements InvocationHandler {
    private Object object;
    private Class proxyClass;

    CriteriaInvocationHandler(Object object) {
        this.object = object;
        this.proxyClass = object.getClass();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Method invokeMethod = null;
            if (method.getName().equals("addOrder")) {
                invokeMethod = proxyClass.getMethod(method.getName(), VersionConfigProperty.get().getOrder());
            } else if (method.getName().equals("add")) {
                invokeMethod = proxyClass.getMethod(method.getName(), VersionConfigProperty.get().getCriterion());
            } else {
                invokeMethod = proxyClass.getMethod(method.getName(), method.getParameterTypes());
            }
            return invokeMethod.invoke(object, args);
        } catch (Throwable t) {
            throwThrowable(t);
            throw t;
        }
    }

    private void throwThrowable(Throwable throwable) {
        if (ReflectUtils.isAssignable(throwable.getClass(), VersionConfigProperty.get().getConstraintViolationException())) {
            throw new SwiftConstraintViolationException(throwable);
        }
        if (ReflectUtils.isAssignable(throwable.getClass(), VersionConfigProperty.get().getNonUniqueObjectException())) {
            throw new SwiftNonUniqueObjectException(throwable);
        }
        if (null != throwable.getCause()) {
            throwThrowable(throwable.getCause());
        } else {
            return;
        }
    }
}
