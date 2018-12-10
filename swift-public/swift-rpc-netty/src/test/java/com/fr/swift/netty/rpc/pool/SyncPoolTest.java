package com.fr.swift.netty.rpc.pool;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.netty.rpc.CalculatorService;
import com.fr.swift.netty.rpc.invoke.RPCInvokerCreator;


/**
 * This class created on 2018/8/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SyncPoolTest {
    public static void main(String[] args) {
//        SimpleWork.checkIn(System.getProperty("user.dir"));
        SwiftContext.get().init();
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new RPCInvokerCreator()));
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        CalculatorService calculatorService = proxyFactory.getProxy(CalculatorService.class);
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
