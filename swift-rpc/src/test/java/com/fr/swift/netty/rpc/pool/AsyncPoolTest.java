package com.fr.swift.netty.rpc.pool;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.netty.rpc.CalculatorService;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.netty.rpc.proxy.RPCProxyFactory;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.workspace.simple.SimpleWork;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class created on 2018/8/2
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class AsyncPoolTest {
    public static void main(String[] args) {
        SimpleWork.checkIn(System.getProperty("user.dir"));
        ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        Invoker invoker = proxyFactory.getInvoker(null, CalculatorService.class, new RPCUrl(new RPCDestination("192.168.0.28:7000")), false);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        SwiftContext.init();
        RpcServer rpcServer = SwiftContext.get().getBean(RpcServer.class);
        Method method = rpcServer.getMethodByName("add");

        final AtomicInteger count = new AtomicInteger(1);
        while (true) {
            try {
                Result result = invoker.invoke(new SwiftInvocation(method, new Object[]{10, 20, 5000l}));
                RpcFuture future = (RpcFuture) result.getValue();
                future.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        System.out.println(count.getAndIncrement() + ":" + result);
                    }

                    @Override
                    public void fail(Exception e) {
                        System.out.println(count.getAndIncrement() + ":" + e);
                    }
                });

                result = invoker.invoke(new SwiftInvocation(method, new Object[]{10, 20, 5000l}));
                future = (RpcFuture) result.getValue();
                future.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        System.out.println(count.getAndIncrement() + ":" + result);
                    }

                    @Override
                    public void fail(Exception e) {
                        System.out.println(count.getAndIncrement() + ":" + e);
                    }
                });
                Thread.sleep(1000l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
