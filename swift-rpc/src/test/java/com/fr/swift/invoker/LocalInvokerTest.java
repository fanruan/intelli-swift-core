package com.fr.swift.invoker;

import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.local.LocalUrl;
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
            ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
            Invoker invoker = proxyFactory.getInvoker(new TestInvokerImpl("10001", "lucifer", 20), TestInvokerImpl.class, new LocalUrl());
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
        try {
            ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
            Invoker<TestInvokerInterface> invoker = proxyFactory.getInvoker(new TestInvokerImpl("10001", "lucifer", 20), TestInvokerInterface.class, new LocalUrl());
            long nowTime = System.currentTimeMillis();
            TestInvokerInterface proxy = proxyFactory.getProxy(invoker);
            assertEquals(proxy.print(nowTime), nowTime + "10001" + "lucifer" + 20);
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
