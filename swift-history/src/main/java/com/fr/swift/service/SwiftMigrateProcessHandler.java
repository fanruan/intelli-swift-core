package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.MigrateProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cluster.base.node.ClusterNode;
import com.fr.swift.local.LocalUrl;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Moira
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
    protected URL processUrl(Target[] targets, Object... args) throws Exception {
        String[] split = args[1].toString().split(":");
        if (split.length > 1) {
            args[1] = split[1];
            Map<String, ClusterNode> onlineNodes = nodeContainer.getOnlineNodes();
            return UrlSelector.getInstance().getFactory().getURL(onlineNodes.get(split[0]));
        } else {
            return new LocalUrl();
        }
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        URL url = processUrl(targets, args);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();

        final Invoker<?> invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
        RpcFuture<?> rpcFuture = (RpcFuture<?>) invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
        rpcFuture.addCallback(new AsyncRpcCallback() {
            @Override
            public void success(final Object result) {
            }

            @Override
            public void fail(Exception e) {
            }
        });
        return null;
    }

}
