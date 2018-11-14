package com.fr.swift.invoker;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.property.SwiftProperty;
import junit.framework.TestCase;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class InvokerTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ProxyServiceRegistry.INSTANCE.registerService(new TestInvokerImpl());
        ProxyProcessHandlerRegistry.INSTANCE.addHandler(ProcessHandler.class, TestInvokerProcessHandler.class);
    }

    /**
     * 单机直接本地调用
     * 执行对象print方法
     */
    public void testLocalInvoker() {
        SwiftProperty.getProperty().setCluster(false);
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(null));
        TestInvokerInterface proxy = ProxySelector.getInstance().getFactory().getProxy(TestInvokerInterface.class);
        long time = System.currentTimeMillis();
        assertEquals(proxy.print("1", "test", 20, time), "1test20" + time);
    }

    /**
     * 集群调用远程方法
     * 执行handler的processResult 返回null
     */
    public void testRemoteInvoker() {
        SwiftProperty.getProperty().setCluster(true);
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(null));
        TestInvokerInterface proxy = ProxySelector.getInstance().getFactory().getProxy(TestInvokerInterface.class);
        long time = System.currentTimeMillis();
        assertNull(proxy.print("1", "test", 20, time));
    }
}
