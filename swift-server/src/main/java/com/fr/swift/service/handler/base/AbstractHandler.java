package com.fr.swift.service.handler.base;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.selector.ProxySelector;

/**
 * @author yee
 * @date 2018/6/14
 */
public abstract class AbstractHandler<E extends SwiftRpcEvent> implements Handler<E> {

    protected RpcServer rpcServer = SwiftContext.getInstance().getBean(RpcServer.class);

    protected RpcFuture runAsyncRpc(String address, Class serviceClass, String method, Object... args) {
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, serviceClass, new RPCUrl(new RPCDestination(address)), false);
        Result result = invoker.invoke(new SwiftInvocation(rpcServer.getMethodByName(method), args));
        return (RpcFuture) result.getValue();
    }
}
