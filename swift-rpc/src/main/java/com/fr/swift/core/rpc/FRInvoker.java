package com.fr.swift.core.rpc;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.engine.rpc.base.FineResult;
import com.fr.cluster.engine.ticket.FineClusterToolKit;
import com.fr.cluster.rpc.base.InvokeHandler;
import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.SwiftResult;

import java.lang.reflect.InvocationTargetException;
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
            FineResult fineResult = (FineResult) doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
            if (fineResult.getException() == null) {
                return new SwiftResult(fineResult.get());
            } else {
                throw fineResult.getException();
            }
        } catch (InvocationTargetException e) {
            return new SwiftResult(e);
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
        com.fr.cluster.rpc.base.Invoker frInvoker = FineClusterToolKit.getInstance().getInvokerFactory().create(proxy);
        Method method = proxy.getClass().getMethod(methodName, parameterTypes);
        com.fr.cluster.rpc.base.Invocation invocation = com.fr.cluster.rpc.base.Invocation.create(method, arguments);
        if (url.getDestination() != null) {
            ClusterNode clusterNode = ClusterBridge.getView().getNodeById(url.getDestination().getId());
            if (sync) {
                com.fr.cluster.rpc.base.Result result = frInvoker.invoke(clusterNode, invocation);
                return result;
            } else {
                final RpcFuture rpcFuture = new FRFuture();
                InvokeHandler invokeHandler = new InvokeHandler() {
                    @Override
                    public void done(ClusterNode clusterNode, com.fr.cluster.rpc.base.Invocation invocation, com.fr.cluster.rpc.base.Result result) {
                        rpcFuture.done(result);
                    }

                    @Override
                    public void finish() {
                    }
                };
                frInvoker.invoke(clusterNode, invocation, invokeHandler);
                return rpcFuture;
            }
        } else {
            Map<ClusterNode, com.fr.cluster.rpc.base.Result> resultMap = frInvoker.invokeAll(invocation);
            return resultMap;
        }
    }

}
