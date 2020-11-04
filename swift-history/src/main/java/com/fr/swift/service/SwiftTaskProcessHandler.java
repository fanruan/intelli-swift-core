package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.TaskProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Method;

/**
 * @author Heng.J
 * @date 2020/10/23
 * @description
 * @since swift-1.2.0
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(TaskProcessHandler.class)
public class SwiftTaskProcessHandler extends BaseProcessHandler implements TaskProcessHandler {

    public SwiftTaskProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected URL processUrl(Target[] targets, Object... args) {
        if (nodeContainer.getOnlineNodes().containsKey(args[1])) {
            return UrlSelector.getInstance().getFactory().getURL(args[1]);
        }
        SwiftLoggers.getLogger().error("node {} is offline", args[1]);
        return null;
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        URL url = processUrl(targets, args);
        if (url == null) {
            return false;
        }
        final Class<?> proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        Invoker<?> invoker = invokerCreator.createSyncInvoker(proxyClass, url);
        return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
    }
}
