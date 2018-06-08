package com.fr.swift.rpc.test;

import com.fr.swift.ProxyFactory;
import com.fr.swift.rpc.NettyService;
import com.fr.swift.rpc.proxy.RPCProxyFactory;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.selector.ProxySelector;


/**
 * This class created on 2018/6/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClientStart {
    public static void main(String[] args) throws Exception {
        ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        NettyService helloService = proxyFactory.getProxy(null, NettyService.class, new RPCUrl(new RPCDestination("192.168.0.28:8000")));
        String result = helloService.print("lucifer");
        System.out.println(result);
    }
}
