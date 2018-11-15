package com.fr.swift.invoker;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.local.LocalInvokerCreater;
import com.fr.swift.property.SwiftProperty;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class, ProxyServiceRegistry.class, ProxyProcessHandlerRegistry.class})
public class InvokerTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        PowerMock.mockStatic(SwiftProperty.class);
        SwiftProperty swiftProperty = PowerMock.createMock(SwiftProperty.class);
        EasyMock.expect(SwiftProperty.getProperty()).andReturn(swiftProperty).anyTimes();
        PowerMock.replay(SwiftProperty.class);

        PowerMock.mockStatic(ProxyServiceRegistry.class);
        ServiceRegistry serviceRegistry = PowerMock.createMock(ServiceRegistry.class);
        EasyMock.expect(ProxyServiceRegistry.get()).andReturn(serviceRegistry).anyTimes();
        PowerMock.replay(ProxyServiceRegistry.class);
        EasyMock.expect(ProxyServiceRegistry.get().getService(TestInvokerInterface.class)).andReturn(new TestInvokerImpl()).anyTimes();
        EasyMock.replay(ProxyServiceRegistry.get());

        PowerMock.mockStatic(ProxyProcessHandlerRegistry.class);
        ProcessHandlerRegistry processHandlerRegistry = PowerMock.createMock(ProcessHandlerRegistry.class);
        EasyMock.expect(ProxyProcessHandlerRegistry.get()).andReturn(processHandlerRegistry).anyTimes();
        PowerMock.replay(ProxyProcessHandlerRegistry.class);
        EasyMock.expect(processHandlerRegistry.getHandler(ProcessHandler.class)).andReturn(TestInvokerProcessHandler.class).anyTimes();
        EasyMock.replay(ProxyProcessHandlerRegistry.get());
    }


    /**
     * 单机直接本地调用
     * 执行对象print方法
     */
    @Test
    public void testLocalInvoker() {
        EasyMock.expect(SwiftProperty.getProperty().isCluster()).andReturn(false).anyTimes();
        EasyMock.replay(SwiftProperty.getProperty());

        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new LocalInvokerCreater()));
        TestInvokerInterface proxy = ProxySelector.getInstance().getFactory().getProxy(TestInvokerInterface.class);
        long time = System.currentTimeMillis();
        assertEquals(proxy.print("1", "test", 20, time), "1test20" + time);
    }

    /**
     * 集群调用远程方法
     * 执行handler的processResult 返回null
     */
    public void testRemoteInvoker() {
        EasyMock.expect(SwiftProperty.getProperty().isCluster()).andReturn(true).anyTimes();
        EasyMock.replay(SwiftProperty.getProperty());

        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(null));
        TestInvokerInterface proxy = ProxySelector.getInstance().getFactory().getProxy(TestInvokerInterface.class);
        long time = System.currentTimeMillis();
        assertNull(proxy.print("1", "test", 20, time));
    }
}
