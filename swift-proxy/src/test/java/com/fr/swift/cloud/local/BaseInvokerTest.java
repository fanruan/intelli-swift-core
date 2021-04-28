package com.fr.swift.cloud.local;

import com.fr.swift.cloud.basics.ProcessHandler;
import com.fr.swift.cloud.basics.ServiceRegistry;
import com.fr.swift.cloud.basics.annotation.InvokeMethod;
import com.fr.swift.cloud.basics.annotation.Target;
import com.fr.swift.cloud.basics.base.ProxyServiceRegistry;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;

/**
 * This class created on 2018/11/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class BaseInvokerTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        PowerMock.mockStatic(ProxyServiceRegistry.class);
        ServiceRegistry serviceRegistry = PowerMock.createMock(ServiceRegistry.class);
        EasyMock.expect(ProxyServiceRegistry.get()).andReturn(serviceRegistry).anyTimes();
        PowerMock.replay(ProxyServiceRegistry.class);
        EasyMock.expect(ProxyServiceRegistry.get().getService(ITestInvoker.class.getName())).andReturn(new ITestInvokerImpl()).anyTimes();
        EasyMock.replay(ProxyServiceRegistry.get());
    }
}

interface ITestInvoker {
    @InvokeMethod(value = ProcessHandler.class, target = Target.ALL)
    String print(String id, String name, int age, long time);
}

