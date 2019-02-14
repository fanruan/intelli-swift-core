package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.util.Assert;

import java.lang.reflect.Method;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
class SwiftInvokerHandler extends AbstractInvokerHandler {

    protected SwiftInvokerHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Assert.isTrue(method.isAnnotationPresent(InvokeMethod.class), "Method must be wrapped by " + InvokeMethod.class);
        InvokeMethod invokeMethod = method.getAnnotation(InvokeMethod.class);
        Class<? extends ProcessHandler> handlerInterface = invokeMethod.value();
        Class<? extends ProcessHandler> handlerClass = ProxyProcessHandlerRegistry.get().getHandler(handlerInterface);

        ProcessHandler handler = ProxyProcessHandlerPool.get().getProcessHandler(handlerClass, invokerCreator);
        return handler.processResult(method, invokeMethod.target(), args);
    }
}
