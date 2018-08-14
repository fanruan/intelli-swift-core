package com.fr.swift.service.handler.base;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;

/**
 * @author yee
 * @date 2018/6/14
 */
public abstract class AbstractHandler<E extends SwiftRpcEvent> implements Handler<E> {

    protected RpcServer rpcServer = SwiftContext.get().getBean(RpcServer.class);

    protected RpcFuture runAsyncRpc(String address, Class serviceClass, String method, Object... args) throws Exception {
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, serviceClass, new RPCUrl(new RPCDestination(address)), false);
        Result result = invoker.invoke(new SwiftInvocation(rpcServer.getMethodByName(method), args));
        RpcFuture future = (RpcFuture) result.getValue();
        if (null == future) {
            throw new Exception(result.getException());
        }
        return future;
    }
}
