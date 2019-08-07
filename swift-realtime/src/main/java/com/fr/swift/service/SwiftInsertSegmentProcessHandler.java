package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.handler.InsertSegmentProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;

import java.lang.reflect.Method;

/**
 * @author anchore
 * @date 2018/11/13
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(InsertSegmentProcessHandler.class)
public class SwiftInsertSegmentProcessHandler extends AbstractProcessHandler<URL> implements InsertSegmentProcessHandler {

    public SwiftInsertSegmentProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected URL processUrl(Target[] targets, Object... args) {
        // 直接走本地realtime service
        return null;
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] proxyMethodParamTypes = method.getParameterTypes();

        Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, processUrl(targets, args));
        return invoke(invoker, proxyClass, method, method.getName(), proxyMethodParamTypes, args);
    }
}