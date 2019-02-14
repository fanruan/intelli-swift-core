package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/24
 * @describe single or cluster invokerHandler's static agent
 */
public class JdkProxyInvokeHandler implements InvokerHandler, InvocationHandler {

    private InvokerHandler invokerHandler;

    public JdkProxyInvokeHandler(InvokerCreator invokerCreator) {
//        if (invokerCreator.getType() == InvokerType.LOCAL) {
//            this.invokerHandler = new LocalInvokerHandler(invokerCreator);
//        } else {
        this.invokerHandler = new SwiftInvokerHandler(invokerCreator);
//        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invokerHandler.invoke(proxy, method, args);
    }
}
