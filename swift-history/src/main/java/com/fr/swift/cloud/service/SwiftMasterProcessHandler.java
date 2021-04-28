package com.fr.swift.cloud.service;

import com.fr.swift.cloud.basic.URL;
import com.fr.swift.cloud.basics.Invoker;
import com.fr.swift.cloud.basics.InvokerCreator;
import com.fr.swift.cloud.basics.annotation.RegisteredHandler;
import com.fr.swift.cloud.basics.annotation.Target;
import com.fr.swift.cloud.basics.base.handler.BaseProcessHandler;
import com.fr.swift.cloud.basics.base.selector.UrlSelector;
import com.fr.swift.cloud.basics.handler.MasterProcessHandler;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;

import java.lang.reflect.Method;

/**
 * @author Heng.J
 * @date 2020/11/2
 * @description
 * @since swift-1.2.0
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(MasterProcessHandler.class)
public class SwiftMasterProcessHandler extends BaseProcessHandler implements MasterProcessHandler {

    public SwiftMasterProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        final Class<?> proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        URL masterUrl = processUrl(targets, args);
        Invoker<?> invoker = invokerCreator.createSyncInvoker(proxyClass, masterUrl);
        return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
    }

    /**
     * 计算master url
     *
     * @param targets
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    protected URL processUrl(Target[] targets, Object... args) throws Exception {
        return UrlSelector.getInstance().getFactory().getURL(nodeContainer.getMasterNode().getId());
    }
}

