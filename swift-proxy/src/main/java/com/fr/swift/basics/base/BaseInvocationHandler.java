package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.annotation.InvokeMethod;

import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/23
 */
public class BaseInvocationHandler implements InvokerHandler {

    private ProcessHandlerRegistry registry;

    public BaseInvocationHandler(ProcessHandlerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvokeMethod invokeMethod = method.getAnnotation(InvokeMethod.class);
        if (null != invokeMethod) {
            ProcessHandler handler = registry.getHandler(invokeMethod.value());
            return handler.processResult(method, invokeMethod.target(), args);
        }
        throw new UnsupportedOperationException("Method must wrapped by " + InvokeMethod.class);
    }

}
