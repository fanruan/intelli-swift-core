package com.fr.swift.invoker;

import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerPool;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.JdkProxyFactory;
import com.fr.swift.basics.base.ProxyProcessHandlerPool;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.netty.rpc.invoke.RPCInvoker;
import com.fr.swift.netty.rpc.invoke.RPCInvokerCreator;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.swift.property.SwiftProperty;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

/**
 * This class created on 2018/11/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class, ProxyServiceRegistry.class, ProxyProcessHandlerPool.class})
public class SwiftInvokerHandlerTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        //mock swiftProperty
        PowerMock.mockStatic(SwiftProperty.class);
        SwiftProperty swiftProperty = PowerMock.createMock(SwiftProperty.class);
        EasyMock.expect(SwiftProperty.getProperty()).andReturn(swiftProperty).anyTimes();
        PowerMock.replay(SwiftProperty.class);

        //mock ProxyServiceRegistry
        PowerMock.mockStatic(ProxyServiceRegistry.class);
        ServiceRegistry serviceRegistry = PowerMock.createMock(ServiceRegistry.class);
        EasyMock.expect(ProxyServiceRegistry.get()).andReturn(serviceRegistry).anyTimes();
        PowerMock.replay(ProxyServiceRegistry.class);
        EasyMock.expect(ProxyServiceRegistry.get().getService(ISwiftInvokerHandlerTest.class.getName())).andReturn(new ISwiftInvokerHandlerTest() {
            @Override
            public String print(String id, long time) {
                return id + time;
            }
        }).anyTimes();
        EasyMock.replay(ProxyServiceRegistry.get());

        //mock ProxyProcessHandlerPool
        PowerMock.mockStatic(ProxyProcessHandlerPool.class);
        ProcessHandlerPool processHandlerPool = PowerMock.createMock(ProcessHandlerPool.class);
        EasyMock.expect(ProxyProcessHandlerPool.get()).andReturn(processHandlerPool).anyTimes();
        PowerMock.replay(ProxyProcessHandlerPool.class);
    }

    /**
     * cluster下远程调用
     * 目标是本机，返回localinvoker
     * expect:LocalInvoker.class.getName()
     * actual:LocalInvoker.class.getName()
     */
    public void testRemoteInvokerWithLocalUrl() {
        EasyMock.expect(SwiftProperty.getProperty().getClusterId()).andReturn("master").anyTimes();
        EasyMock.replay(SwiftProperty.getProperty());

        InvokerCreator invokerCreator = new RPCInvokerCreator();
        try {
            EasyMock.expect(ProxyProcessHandlerPool.get().getProcessHandler(EasyMock.anyObject(Class.class), EasyMock.anyObject(InvokerCreator.class))).andReturn(new SwiftInvokerHandlerTestHandler(invokerCreator)).anyTimes();
            EasyMock.replay(ProxyProcessHandlerPool.get());
        } catch (Exception e) {
            assertTrue(false);
        }
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(invokerCreator));
        ISwiftInvokerHandlerTest proxy = ProxySelector.getInstance().getFactory().getProxy(ISwiftInvokerHandlerTest.class);
        long time = System.currentTimeMillis();
        assertEquals(proxy.print("1", time), LocalInvoker.class.getName());
    }

    /**
     * cluster下远程调用
     * 目标不是本机，返回rpcInvoker
     * expect:RPCInvoker.class.getName()
     * actual:RPCInvoker.class.getName()
     */
    public void testRemoteInvokerWithRemoteUrl() {
        EasyMock.expect(SwiftProperty.getProperty().getClusterId()).andReturn("slave").anyTimes();
        EasyMock.replay(SwiftProperty.getProperty());

        InvokerCreator invokerCreator = new RPCInvokerCreator();
        try {
            EasyMock.expect(ProxyProcessHandlerPool.get().getProcessHandler(EasyMock.anyObject(Class.class), EasyMock.anyObject(InvokerCreator.class))).andReturn(new SwiftInvokerHandlerTestHandler(invokerCreator)).anyTimes();
            EasyMock.replay(ProxyProcessHandlerPool.get());
        } catch (Exception e) {
            assertTrue(false);
        }
        ProxySelector.getInstance().switchFactory(new JdkProxyFactory(invokerCreator));
        ISwiftInvokerHandlerTest proxy = ProxySelector.getInstance().getFactory().getProxy(ISwiftInvokerHandlerTest.class);
        long time = System.currentTimeMillis();
        assertEquals(proxy.print("1", time), RPCInvoker.class.getName());
    }
}

interface ISwiftInvokerHandlerTest {
    @InvokeMethod(value = ProcessHandler.class, target = Target.NONE)
    String print(String id, long time);
}

class SwiftInvokerHandlerTestHandler extends AbstractProcessHandler {

    public SwiftInvokerHandlerTestHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] target, Object... args) throws Throwable {
        return invokerCreator.createSyncInvoker(method.getDeclaringClass(), new RPCUrl(new RPCDestination("master"))).getClass().getName();
    }

    @Override
    protected Object processUrl(Target[] target, Object... args) {
        return null;
    }
}