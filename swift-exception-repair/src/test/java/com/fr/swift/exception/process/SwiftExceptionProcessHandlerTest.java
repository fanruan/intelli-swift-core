package com.fr.swift.exception.process;

import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.exception.ExceptionContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.ExceptionInfoBean;
import com.fr.swift.exception.ExceptionInfoType;
import com.fr.swift.exception.service.ExceptionHandleService;
import com.fr.swift.local.LocalInvoker;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Marvin
 * @date 8/22/2019
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DummyClusterSelector.class, SwiftExceptionProcessHandler.class})
@ProxyService(SwiftExceptionProcessHandlerTest.class)
public class SwiftExceptionProcessHandlerTest extends TestCase {

    InvokerCreator invokerCreator;
    ExceptionInfoBean bean;
    URL url;
    public static boolean flag = false;

    @Before
    public void setUp() throws Exception {
        ProxyServiceRegistry.get().registerService(new SwiftExceptionProcessHandlerTest());
        invokerCreator = PowerMockito.mock(InvokerCreator.class);
        url = PowerMockito.mock(URL.class);
        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftExceptionProcessHandlerTest.class.getName()), SwiftExceptionProcessHandlerTest.class, null, false);
        PowerMockito.when(invokerCreator.createAsyncInvoker(SwiftExceptionProcessHandlerTest.class, url)).thenReturn(invoker);
        PowerMockito.when(invokerCreator.createAsyncInvoker(ExceptionHandleService.class, url)).thenReturn(invoker);
        PowerMockito.mockStatic(DummyClusterSelector.class);
        DummyClusterSelector selector = PowerMockito.mock(DummyClusterSelector.class);
        PowerMockito.when(DummyClusterSelector.getInstance()).thenReturn(selector);
        PowerMockito.when(selector.selectCluster((ExceptionInfo) ArgumentMatchers.any())).thenReturn("ABC");
        bean = new ExceptionInfoBean.Builder()
                .setNowAndHere()
                .setContext(PowerMockito.mock(ExceptionContext.class))
                .setType(ExceptionInfoType.UPLOAD_SEGMENT).build();
    }

    @Test
    public void testProcessUrl() {
        SwiftExceptionProcessHandler handler = new SwiftExceptionProcessHandler(invokerCreator);
        URL url = handler.processUrl(null, bean);
        assertNull(url);
    }

    @Test
    public void testProcessResult() throws Throwable {
        SwiftExceptionProcessHandler spyHandler = PowerMockito.spy((new SwiftExceptionProcessHandler(invokerCreator)));
        Destination destination = PowerMockito.mock(Destination.class);
        PowerMockito.when(url.getDestination()).thenReturn(destination);
        PowerMockito.when(destination.getId()).thenReturn("LOCAL");
        Target[] targets = new Target[]{Target.ALL};
        PowerMockito.doReturn(url).when(spyHandler, "processUrl", targets, bean);
        spyHandler.processResult(
                SwiftExceptionProcessHandlerTest.class.getMethod("ExceptionProcess", ExceptionInfo.class), targets, bean);
        assertTrue(flag);
    }

    public void ExceptionProcess(ExceptionInfo info) {
        flag = true;
    }

}
