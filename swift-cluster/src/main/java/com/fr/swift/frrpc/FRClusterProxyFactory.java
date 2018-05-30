package com.fr.swift.frrpc;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.URL;
import com.fr.swift.invoker.SwiftInvoker;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRClusterProxyFactory implements ProxyFactory {

    /**
     * fr rpc demo
     * ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
     * Invoker<ClusterService> invoker = proxyFactory.getInvoker(INSTANCE, ClusterService.class, new FRUrl(null));
     * ClusterService proxy = proxyFactory.getProxy(invoker);
     * proxy.rpcSend(ClusterNodeManager.getInstance().getCurrentId(), System.currentTimeMillis());
     */
    //fixme 暂时远程方法直接调用fr的，正确的应该在外再包一层动态代理。
    @Override
    public <T> T getProxy(Invoker<T> invoker) throws Exception {
        T proxy = (T) FRProxyCache.getProxy(invoker.getInterface());
        return proxy;
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new SwiftInvoker<>(proxy, type, url);
    }
}
