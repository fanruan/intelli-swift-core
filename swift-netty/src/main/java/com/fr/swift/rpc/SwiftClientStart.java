package com.fr.swift.rpc;

import com.fr.swift.rpc.client.RpcProxy;
import com.fr.swift.rpc.registry.ServiceDiscovery;
import com.fr.third.springframework.context.ApplicationContext;
import com.fr.third.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClientStart {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = new RpcProxy((ServiceDiscovery) context.getBean("serviceDiscovery"));
        NettyService helloService = rpcProxy.create(NettyService.class);
        String result = helloService.print("lucifer");
        System.out.println(result);
    }
}
