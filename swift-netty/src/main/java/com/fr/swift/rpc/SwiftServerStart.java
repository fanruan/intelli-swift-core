package com.fr.swift.rpc;

import com.fr.swift.ClusterService;
import com.fr.swift.node.SwiftClusterNodeImpl;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.rpc.zookeeper.ZkClusterService;
import com.fr.third.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServerStart {

    public static void main(String[] args) throws Exception {
//        new SwiftEngineActivator().start();
        ClusterService clusterService = new ZkClusterService("192.168.0.24:2181", new SwiftClusterNodeImpl("1", "2", "3", 4));
        clusterService.competeMaster();
        clusterService.registerNode();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcServer rpcServer = (RpcServer) context.getBean("rpcServer");
        rpcServer.initService(context);
        rpcServer.start();

    }
}
