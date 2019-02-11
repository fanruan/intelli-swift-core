package com.fr.swift.basic.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.handler.SwiftAppointProcessHandler;
import com.fr.swift.local.LocalInvoker;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/1/14
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@ProxyService(SwiftAppointProcessHandlerTest.class)
public class SwiftAppointProcessHandlerTest extends TestCase {

    InvokerCreator invokerCreator;

    @Override
    public void setUp() throws Exception {
        ProxyServiceRegistry.get().registerService(new SwiftAppointProcessHandlerTest());
        invokerCreator = EasyMock.createMock(InvokerCreator.class);
        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftAppointProcessHandlerTest.class.getName()), SwiftAppointProcessHandlerTest.class, null, false);
        EasyMock.expect(invokerCreator.createAsyncInvoker(SwiftAppointProcessHandlerTest.class, null)).andReturn(invoker).anyTimes();
        EasyMock.replay(invokerCreator);
    }

    public void testProcessUrl() {
        SwiftAppointProcessHandler handler = new SwiftAppointProcessHandler(invokerCreator);
        Collection<URL> urls = handler.processUrl(null, Collections.singleton("test"));
        assertEquals(urls.size(), 1);
        List<URL> urlList = new ArrayList<URL>(urls);
        assertNull(urlList.get(0));
    }

    public void testProcessResult() {
        SwiftAppointProcessHandler handler = new SwiftAppointProcessHandler(invokerCreator);
        try {
            Set<String> result = (Set<String>) handler.processResult(
                    SwiftAppointProcessHandlerTest.class.getMethod("appointMethod", List.class, String.class), new Target[]{Target.ANALYSE}, Collections.singletonList("test"), "test");
            assertEquals(result.size(), 1);
            List<String> urlList = new ArrayList<String>(result);
            assertEquals(urlList.get(0), "test:appointMethod");
        } catch (Throwable throwable) {
            assertTrue(false);
        }
    }

    public String appointMethod(List<String> sets, String appointStr) {
        return appointStr + ":appointMethod";
    }

}
