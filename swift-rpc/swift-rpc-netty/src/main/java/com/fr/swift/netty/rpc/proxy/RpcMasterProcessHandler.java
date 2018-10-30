package com.fr.swift.netty.rpc.proxy;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.handler.BaseMasterProcessHandler;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.netty.rpc.invoke.RPCInvoker;

/**
 * @author yee
 * @date 2018/10/25
 */
public class RpcMasterProcessHandler extends BaseMasterProcessHandler {
    @Override
    protected Invoker createInvoker(Class tClass, URL url) {
        return new RPCInvoker(SwiftContext.get().getBean(tClass), tClass, url);
    }
}
