package com.fr.swift.local;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.property.SwiftProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftProperty.class, ProxyServiceRegistry.class, ProxyProcessHandlerRegistry.class, SwiftContext.class})
public class InvokerTest {

    InvokerCreator invokerCreator = new LocalInvokerCreator();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftProperty.class, ProxyServiceRegistry.class, ProxyProcessHandlerRegistry.class, SwiftContext.class);

        ServiceRegistry serviceRegistry = Mockito.mock(ServiceRegistry.class);
        Mockito.when(ProxyServiceRegistry.get()).thenReturn(serviceRegistry);
        Mockito.when(ProxyServiceRegistry.get().getService(ITestInvoker.class.getName())).thenReturn(new ITestInvokerImpl());

        SwiftProperty swiftProperty = Mockito.mock(SwiftProperty.class);
        Mockito.when(SwiftProperty.get()).thenReturn(swiftProperty);

        ProcessHandlerRegistry processHandlerRegistry = Mockito.mock(ProcessHandlerRegistry.class);
        Mockito.when(ProxyProcessHandlerRegistry.get()).thenReturn(processHandlerRegistry);
        Mockito.when(processHandlerRegistry.getHandler(ProcessHandler.class)).thenReturn(TestInvokerProcessHandler.class);

        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(beanFactory.getBean(TestInvokerProcessHandler.class, invokerCreator)).thenReturn(new TestInvokerProcessHandler(invokerCreator));
    }


    /**
     * 单机直接本地调用
     * 执行对象print方法
     */
    @Test
    public void testLocalInvoker() {
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(invokerCreator));
        ITestInvoker proxy = ProxySelector.getInstance().getFactory().getProxy(ITestInvoker.class);
        long time = System.currentTimeMillis();
        Assert.assertEquals(proxy.print("1", "test", 20, time), "1test20" + time);
    }
}