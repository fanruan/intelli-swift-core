package com.fr.swift.netty.rpc.server;

import com.fr.swift.SwiftContext;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcServerServiceStarter implements NettyServiceStarter {

    private RpcServer rpcServer;

    public RpcServerServiceStarter() {
    }

    @Override
    public void start() throws Exception {
        rpcServer = SwiftContext.get().getBean(RpcServer.class);
//        rpcServer.initService(context);
        rpcServer.start();
    }

    // TODO: 2018/6/8 待实现
    @Override
    public void stop() {
    }
}
