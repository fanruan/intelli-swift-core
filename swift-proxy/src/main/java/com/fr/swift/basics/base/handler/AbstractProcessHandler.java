package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.SwiftInvocation;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/10/24
 */
public abstract class AbstractProcessHandler<T> implements ProcessHandler {

    private static final String TO_STRING = "toString";
    private static final String HASH_CODE = "hashCode";
    private static final String EQUALS = "equals";

    protected InvokerCreator invokerCreator;

    public AbstractProcessHandler(InvokerCreator invokerCreator) {
        this.invokerCreator = invokerCreator;
    }

    /**
     * process targets url
     * 只负责各种形式的url计算。
     *
     * @return
     */
    protected abstract T processUrl(Target[] targets, Object... args);

    /**
     * @param invoker
     * @param proxyClass
     * @param method
     * @param methodName     传了method还传methodName是不想有循环method.getName调几次
     * @param parameterTypes
     * @param args
     * @return
     * @throws Throwable
     */
    protected Object invoke(Invoker invoker, Class proxyClass, Method method, String methodName, Class[] parameterTypes, Object... args) throws Throwable {

        if (proxyClass == Object.class) {
            return method.invoke(invoker, args);
        }
        if (TO_STRING.equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if (HASH_CODE.equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if (EQUALS.equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        return invoker.invoke(new SwiftInvocation(method, args)).recreate();
    }

    protected Object handleAsyncResult(Object obj) throws Throwable {
        if (obj instanceof RpcFuture) {
            RpcFuture future = (RpcFuture) obj;
            final CountDownLatch latch = new CountDownLatch(1);
            final Object[] result = new Object[1];
            future.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(Object o) {
                    result[0] = o;
                    latch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    result[0] = e;
                    latch.countDown();
                }
            });
            latch.await();
            if (result[0] instanceof Exception) {
                throw (Exception) result[0];
            }
            return result[0];
        } else {
            return obj;
        }
    }
}
