package com.fr.swift.netty.rpc.client;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.netty.rpc.NettyService;
import com.fr.swift.netty.rpc.invoke.RPCInvokerCreator;

import java.util.UUID;

/**
 * This class created on 2018/8/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LengthTest {
    public static void main(String[] args) {
//        SimpleWork.checkIn(System.getProperty("user.dir"));
        SwiftContext.get().init();
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new RPCInvokerCreator()));
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        NettyService nettyService = proxyFactory.getProxy(NettyService.class);
        long startTime = System.currentTimeMillis();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 3000000; i++) {
            string.append(UUID.randomUUID().toString());
        }
        String value = nettyService.print(string.toString());
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
