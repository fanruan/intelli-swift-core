package com.fr.swift.local;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.property.SwiftProperty;
import org.easymock.EasyMock;
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
public class InvokerTest extends BaseInvokerTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        //mock swiftProperty
        PowerMock.mockStatic(SwiftProperty.class);
        SwiftProperty swiftProperty = PowerMock.createMock(SwiftProperty.class);
        EasyMock.expect(SwiftProperty.getProperty()).andReturn(swiftProperty).anyTimes();
        PowerMock.replay(SwiftProperty.class);

        PowerMock.mockStatic(ProxyProcessHandlerRegistry.class);
        ProcessHandlerRegistry processHandlerRegistry = PowerMock.createMock(ProcessHandlerRegistry.class);
        EasyMock.expect(ProxyProcessHandlerRegistry.get()).andReturn(processHandlerRegistry).anyTimes();
        PowerMock.replay(ProxyProcessHandlerRegistry.class);
        EasyMock.expect(processHandlerRegistry.getHandler(ProcessHandler.class)).andReturn(TestInvokerProcessHandler.class).anyTimes();
        EasyMock.replay(processHandlerRegistry);
    }


    /**
     * 单机直接本地调用
     * 执行对象print方法
     */
    public void testLocalInvoker() {
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new LocalInvokerCreator()));
        ITestInvoker proxy = ProxySelector.getInstance().getFactory().getProxy(ITestInvoker.class);
        long time = System.currentTimeMillis();
        assertEquals(proxy.print("1", "test", 20, time), "1test20" + time);
    }
}