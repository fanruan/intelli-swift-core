package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.InvokerType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/24
 * @describe single or cluster invokerHandler's static agent
 */
public class JdkProxyInvokeHandler implements InvokerHandler, InvocationHandler {

    private InvokerHandler invokerHandler;

    public JdkProxyInvokeHandler(InvokerCreater invokerCreater) {
        if (invokerCreater.getType() == InvokerType.LOCAL) {
            this.invokerHandler = new LocalInvokerHandler(invokerCreater);
        } else {
            this.invokerHandler = new RemoteInvokerHandler(invokerCreater);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invokerHandler.invoke(proxy, method, args);
    }
}
