package com.fr.swift.core.rpc;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.rpc.base.ClusterInvokeHandler;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.base.SwiftResult;
import com.fr.swift.local.LocalResult;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class created on 2018/5/30
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRInvoker<T> implements Invoker<T> {

    private final T proxy;

    private final Class<T> type;

    private final URL url;

    private boolean sync = true;

    public FRInvoker(T proxy, Class<T> type, URL url, boolean sync) {
        this(proxy, type, url);
        this.sync = sync;
    }

    public FRInvoker(T proxy, Class<T> type, URL url) {
        if (type == null) {
            throw new IllegalArgumentException("interface == null");
        }
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) {
        try {
            return new SwiftResult(doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments()));
        } catch (Throwable e) {
            return new SwiftResult(e);
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {
    }

    protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
        com.fr.cluster.rpc.base.ClusterInvoker frInvoker = InvokerCache.getInstance().getInvoker(type);
        Method method = proxy.getClass().getMethod(methodName, parameterTypes);
        com.fr.rpc.Invocation invocation = com.fr.rpc.Invocation.create(method, arguments);
        if (url.getDestination() != null) {
            ClusterNode clusterNode = ClusterBridge.getView().getNodeById(url.getDestination().getId());
            if (sync) {
                com.fr.rpc.Result result = frInvoker.invoke(clusterNode, invocation);
                if (result.getException() != null) {
                    throw result.getException();
                } else {
                    return result.get();
                }
            } else {
                final RpcFuture rpcFuture = new FRFuture();
                ClusterInvokeHandler invokeHandler = new ClusterInvokeHandler() {
                    @Override
                    public void done(ClusterNode clusterNode, com.fr.rpc.Invocation invocation, com.fr.rpc.Result result) {
                        rpcFuture.done(new LocalResult(result.get(), result.getException()));
                    }

                    @Override
                    public void finish() {
                    }
                };
                frInvoker.invoke(clusterNode, invocation, invokeHandler);
                return rpcFuture;
            }
        } else {
            Map<ClusterNode, com.fr.rpc.Result> resultMap = frInvoker.invokeAll(invocation);
            return resultMap;
        }
    }

}
