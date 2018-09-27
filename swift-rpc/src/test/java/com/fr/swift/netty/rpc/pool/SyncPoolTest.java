package com.fr.swift.netty.rpc.pool;

import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.netty.rpc.CalculatorService;
import com.fr.swift.netty.rpc.proxy.RPCProxyFactory;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.workspace.simple.SimpleWork;


/**
 * This class created on 2018/8/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SyncPoolTest {
    public static void main(String[] args) throws Exception {
        SimpleWork.checkIn(System.getProperty("user.dir"));
        SwiftContext.init();
        ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        CalculatorService calculatorService = proxyFactory.getProxy(null, CalculatorService.class, new RPCUrl(new RPCDestination("192.168.0.28:7000")));
        int count = 0;
        while (true) {
            try {
                int value = calculatorService.add(10, 20, 5000l);
                System.out.println(count++ + ":" + value);
                value = calculatorService.add(10, 20, 5000l);
                System.out.println(count++ + ":" + value);
                Thread.sleep(1000l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
