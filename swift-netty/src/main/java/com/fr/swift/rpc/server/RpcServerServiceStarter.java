package com.fr.swift.rpc.server;

import com.fr.swift.rpc.RpcServiceStarter;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.third.springframework.context.ApplicationContext;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RpcServerServiceStarter implements RpcServiceStarter {

    private ApplicationContext context;

    private RpcServer rpcServer;

    public RpcServerServiceStarter(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void start() throws Exception {
        rpcServer = (RpcServer) context.getBean("rpcServer");
        rpcServer.initService(context);
        rpcServer.start();
    }

    // TODO: 2018/6/8 待实现
    @Override
    public void stop() throws Exception {
    }
}
