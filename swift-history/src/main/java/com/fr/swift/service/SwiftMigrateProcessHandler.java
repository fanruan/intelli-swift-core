package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.exception.TargetNodeOfflineException;
import com.fr.swift.basics.handler.MigrateProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;

import java.lang.reflect.Method;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(MigrateProcessHandler.class)
public class SwiftMigrateProcessHandler extends BaseProcessHandler implements MigrateProcessHandler {
    public SwiftMigrateProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected URL processUrl(Target[] targets, Object... args) {
        String clusterId = (String) args[1];
        if (nodeContainer.getOnlineNodes().containsKey(clusterId)) {
            return UrlSelector.getInstance().getFactory().getURL(clusterId);
        }
        throw new TargetNodeOfflineException((String) args[1]);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        URL url = processUrl(targets, args);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        final Invoker<?> invoker = invokerCreator.createSyncInvoker(proxyClass, url);
        return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
    }
}
