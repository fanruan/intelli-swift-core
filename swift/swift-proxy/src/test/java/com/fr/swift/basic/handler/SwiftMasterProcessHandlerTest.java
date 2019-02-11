package com.fr.swift.basic.handler;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.handler.SwiftMasterProcessHandler;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.property.SwiftProperty;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class, SwiftContext.class})
@ProxyService(SwiftMasterProcessHandlerTest.class)
public class SwiftMasterProcessHandlerTest extends TestCase {

    InvokerCreator invokerCreator;

    @Override
    public void setUp() throws Exception {
        ProxyServiceRegistry.get().registerService(new SwiftMasterProcessHandlerTest());

        SwiftServiceInfoBean swiftServiceInfoBean = EasyMock.createMock(SwiftServiceInfoBean.class);
        SwiftServiceInfoService swiftServiceInfoService = EasyMock.createMock(SwiftServiceInfoService.class);
        invokerCreator = EasyMock.createMock(InvokerCreator.class);
        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftMasterProcessHandlerTest.class.getName()), SwiftMasterProcessHandlerTest.class, null, true);
        EasyMock.expect(invokerCreator.createSyncInvoker(SwiftMasterProcessHandlerTest.class, null)).andReturn(invoker).anyTimes();

        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        EasyMock.expect(beanFactory.getBean(SwiftServiceInfoService.class)).andReturn(swiftServiceInfoService).anyTimes();
        EasyMock.expect(swiftServiceInfoService.getServiceInfoByService(SwiftServiceInfoService.SERVICE)).andReturn(Collections.singletonList(swiftServiceInfoBean)).anyTimes();
        EasyMock.expect(swiftServiceInfoBean.getServiceInfo()).andReturn("127.0.0.1:8080").anyTimes();
        PowerMock.replay(SwiftContext.class);
        EasyMock.replay(beanFactory, swiftServiceInfoBean, swiftServiceInfoService, invokerCreator);
    }

    public void testProcessUrl() {
        SwiftMasterProcessHandler handler = new SwiftMasterProcessHandler(invokerCreator);
        URL url = handler.processUrl(new Target[]{Target.HISTORY});
        assertNull(url);
    }

    public void testProcessResult() {
        SwiftMasterProcessHandler handler = new SwiftMasterProcessHandler(invokerCreator);
        try {
            String result = (String) handler.processResult(
                    SwiftMasterProcessHandlerTest.class.getMethod("masterMethod"), new Target[]{Target.ANALYSE});
            assertEquals(result, "master");
        } catch (Throwable throwable) {
            assertTrue(false);
        }
    }

    public String masterMethod() {
        return "master";
    }
}
