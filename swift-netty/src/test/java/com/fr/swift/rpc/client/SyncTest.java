package com.fr.swift.rpc.client;

import com.fr.swift.ProxyFactory;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.rpc.CalculatorService;
import com.fr.swift.rpc.proxy.RPCProxyFactory;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.selector.ProxySelector;

/**
 * This class created on 2018/6/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 * 同步rpc
 */
public class SyncTest {
    public static void main(String[] args) {
        SwiftContext.init();
        ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
        //step1: get proxyFactory
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        //step2: create object proxy
        CalculatorService calculatorService = proxyFactory.getProxy(null, CalculatorService.class, new RPCUrl(new RPCDestination("127.0.0.1:8000")));
        long startTime = System.currentTimeMillis();
        //step3: invoke method
        int value = calculatorService.add(10, 20, 5000l);
        System.out.println(value);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
