package com.fr.swift.netty.rpc.client;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.netty.rpc.CalculatorService;
import com.fr.swift.netty.rpc.proxy.RPCProxyFactory;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.workspace.simple.SimpleWork;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * This class created on 2018/6/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 * 异步RPC
 */
public class AsyncTest {

    public static void main(String[] args) throws Exception {
        SimpleWork.checkIn(System.getProperty("user.dir"));
        ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
        //step1: get proxyFactory
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        //step2: create invoker
        Invoker invoker = proxyFactory.getInvoker(null, CalculatorService.class, new RPCUrl(new RPCDestination("192.168.0.28:7000")), false);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
//        Method method = CalculatorService.class.getMethod("add", int.class, int.class, long.class);
        //step3: get invoker method
        SwiftContext.init();
        RpcServer rpcServer = SwiftContext.get().getBean(RpcServer.class);
        Method method = rpcServer.getMethodByName("add");
        final long startTime = System.currentTimeMillis();
        //step4: async invoke method
        Result result = invoker.invoke(new SwiftInvocation(method, new Object[]{10, 20, 5000l}));
        RpcFuture future = (RpcFuture) result.getValue();
        //step4: add callback
        future.addCallback(new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                long successEndTime = System.currentTimeMillis();
                System.out.println(successEndTime - startTime);
                countDownLatch.countDown();
                RpcClient.stop();
            }

            @Override
            public void fail(Exception e) {
                countDownLatch.countDown();
                RpcClient.stop();
            }
        });
        long asyncEndTime = System.currentTimeMillis();
        System.out.println(asyncEndTime - startTime);
        System.out.println(future.get());
    }

}
