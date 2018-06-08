package com.fr.swift.rpc.starter;

import com.fr.swift.rpc.RpcServiceStarter;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.third.springframework.context.ApplicationContext;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 * fixme  先不使用
 */
public class RpcClientServiceStarter implements RpcServiceStarter {

    private ApplicationContext context;

    private RpcServer rpcClient;

    public RpcClientServiceStarter(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void start() throws Exception {
        rpcClient = (RpcServer) context.getBean("rpcClient");
        rpcClient.initService(context);
        rpcClient.start();
    }

    // TODO: 2018/6/8 待实现
    @Override
    public void stop() throws Exception {
    }
}
