package com.fr.swift.basics.base;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.local.LocalProcessHandler;

import java.lang.reflect.Method;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
class LocalInvokerHandler extends AbstractInvokerHandler {

    protected LocalInvokerHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ProcessHandler handler = ProxyProcessHandlerPool.get().getProcessHandler(LocalProcessHandler.class, invokerCreator);
        return handler.processResult(method, new Target[]{Target.NONE}, args);
    }
}
