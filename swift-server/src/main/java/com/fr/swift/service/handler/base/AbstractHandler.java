package com.fr.swift.service.handler.base;

import com.fr.swift.basics.RpcFuture;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.netty.rpc.server.ServiceMethodRegistry;
import com.fr.swift.utils.ClusterCommonUtils;

/**
 * @author yee
 * @date 2018/6/14
 */
public abstract class AbstractHandler<E extends SwiftRpcEvent> implements Handler<E> {

    protected RpcFuture runAsyncRpc(String address, Class serviceClass, String method, Object... args) throws Exception {
        return ClusterCommonUtils.runAsyncRpc(address, serviceClass, ServiceMethodRegistry.INSTANCE.getMethodByName(method), args);
    }
}
