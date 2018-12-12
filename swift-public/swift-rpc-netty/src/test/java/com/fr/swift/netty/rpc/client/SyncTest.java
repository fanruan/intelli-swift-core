package com.fr.swift.netty.rpc.client;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.netty.rpc.CalculatorService;
import com.fr.swift.netty.rpc.invoke.RPCInvokerCreator;

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
//        SimpleWork.checkIn(System.getProperty("user.dir"));
        SwiftContext.get().init();
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new RPCInvokerCreator()));
        //step1: get proxyFactory
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        //step2: create object proxy
        CalculatorService calculatorService = proxyFactory.getProxy(CalculatorService.class);
        long startTime = System.currentTimeMillis();
        //step3: invoke method
        int value = calculatorService.add(10, 20, 5000l);
//        System.out.println(value);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
