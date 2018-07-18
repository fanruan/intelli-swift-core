package com.fr.swift.rpc.client;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.rpc.CalculatorService;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.proxy.RPCProxyFactory;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.selector.ProxySelector;

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

    public static void main(String[] args) {
        ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
        //step1: get proxyFactory
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        //step2: create invoker
        Invoker invoker = proxyFactory.getInvoker(null, CalculatorService.class, new RPCUrl(new RPCDestination("127.0.0.1:8000")), false);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
//        Method method = CalculatorService.class.getMethod("add", int.class, int.class, long.class);
        //step3: get invoker method
        SwiftContext.init();
        RpcServer rpcServer = SwiftContext.get().getBean(RpcServer.class);
        rpcServer.initService(SwiftContext.get());
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
