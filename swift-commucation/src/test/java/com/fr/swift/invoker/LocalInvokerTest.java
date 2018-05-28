package com.fr.swift.invoker;

import com.fr.swift.Invocation;
import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.proxy.LocalProxyFactory;
import com.fr.swift.url.LocalUrl;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalInvokerTest extends TestCase {

    /**
     * 普通的本地invoker使用方法
     */
    public void testLocalInvoker() {
        try {
            ProxyFactory localProxyFactory = new LocalProxyFactory();
            Invoker invoker = localProxyFactory.getInvoker(new TestInvokerImpl("10001", "lucifer", 20), TestInvokerImpl.class, new LocalUrl());
            long nowTime = System.currentTimeMillis();
            Object[] objects = new Object[]{nowTime};
            Method method = TestInvokerImpl.class.getMethod("print", long.class);
            Invocation invocation = new SwiftInvocation(method, objects);
            Result result = invoker.invoke(invocation);
            assertEquals(result.getValue().toString(), nowTime + "10001" + "lucifer" + 20);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /**
     * 基于invoker的动态代理调用方法
     */
    public void testLocalInvokerByProxy() {
        ProxyFactory localProxyFactory = new LocalProxyFactory();
        Invoker<TestInvokerInterface> invoker = localProxyFactory.getInvoker(new TestInvokerImpl("10001", "lucifer", 20), TestInvokerInterface.class, new LocalUrl());
        long nowTime = System.currentTimeMillis();
        TestInvokerInterface proxy = localProxyFactory.getProxy(invoker);
        assertEquals(proxy.print(nowTime), nowTime + "10001" + "lucifer" + 20);
        proxy.toString();
    }
}
