package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreater;

import java.lang.reflect.InvocationHandler;

/**
 * @author yee
 * @date 2018/10/24
 */
public class JdkProxyInvokeHandler extends BaseInvocationHandler implements InvocationHandler {
    public JdkProxyInvokeHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }
}
