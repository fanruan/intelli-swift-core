package com.fr.swift.process.handler;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerType;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.local.LocalInvokerCreator;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.ServiceType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * @author yee
 * @date 2019-01-11
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {SwiftContext.class, ClusterSwiftServerService.class, ProxyServiceRegistry.class})
public class SwiftCommonProcessHandlerTest {

    private Map<String, ClusterEntity> map = new HashMap<String, ClusterEntity>();

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        URL mockURL = PowerMock.createMock(URL.class);
        map.put("clusterId", new ClusterEntity(mockURL, ServiceType.ANALYSE, AnalyseService.class));
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);

        // Generate by Mock Plugin
        SwiftServiceInfoService mockSwiftServiceInfoService = PowerMock.createMock(SwiftServiceInfoService.class);
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftServiceInfoService.class))).andReturn(mockSwiftServiceInfoService).anyTimes();
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        PowerMock.replayAll();
    }

    @Test
    public void processResult() throws Throwable {
        testLocal();
        testForService(ServiceType.ANALYSE, Target.ANALYSE);
        testForService(ServiceType.HISTORY, Target.HISTORY);
        testForService(ServiceType.INDEXING, Target.INDEXING);
        testForService(ServiceType.REAL_TIME, Target.REAL_TIME);
        testForService(ServiceType.SERVER, Target.ALL);
    }


    private void testForService(ServiceType type, Target target) throws Throwable {
        // Generate by Mock Plugin
        PowerMock.mockStatic(ClusterSwiftServerService.class);
        ClusterSwiftServerService mockClusterSwiftServerService = PowerMock.createMock(ClusterSwiftServerService.class);
        EasyMock.expect(ClusterSwiftServerService.getInstance()).andReturn(mockClusterSwiftServerService).anyTimes();
        if (target.equals(Target.ALL)) {
            EasyMock.expect(mockClusterSwiftServerService.getClusterEntityByService(ServiceType.ANALYSE)).andReturn(map).anyTimes();
            EasyMock.expect(mockClusterSwiftServerService.getClusterEntityByService(ServiceType.HISTORY)).andReturn(map).anyTimes();
            EasyMock.expect(mockClusterSwiftServerService.getClusterEntityByService(ServiceType.INDEXING)).andReturn(map).anyTimes();
            EasyMock.expect(mockClusterSwiftServerService.getClusterEntityByService(ServiceType.REAL_TIME)).andReturn(map).anyTimes();
        } else {
            EasyMock.expect(mockClusterSwiftServerService.getClusterEntityByService(type)).andReturn(map).anyTimes();
        }
        PowerMock.replay(ClusterSwiftServerService.class);
// Generate by Mock Plugin
        InvokerCreator mockInvokerCreator = PowerMock.createMock(InvokerCreator.class);

        EasyMock.expect(mockInvokerCreator.createAsyncInvoker(EasyMock.notNull(Class.class), EasyMock.anyObject(URL.class))).andReturn(createInvoker()).anyTimes();
        EasyMock.expect(mockInvokerCreator.getType()).andReturn(InvokerType.REMOTE).anyTimes();
        PowerMock.replayAll();
        new SwiftCommonProcessHandler(mockInvokerCreator).processResult(SwiftCommonProcessHandlerTest.class.getDeclaredMethod("testCommon", String.class), new Target[]{target}, target.name());
// do test
        PowerMock.verifyAll();
    }

    private void testLocal() throws Throwable {
        // Generate by Mock Plugin
        PowerMock.mockStatic(ProxyServiceRegistry.class);
        ProxyServiceRegistry mockProxyServiceRegistry = PowerMock.createMock(ProxyServiceRegistry.class);
        EasyMock.expect(ProxyServiceRegistry.get()).andReturn(mockProxyServiceRegistry).anyTimes();
        EasyMock.expect(mockProxyServiceRegistry.getService(EasyMock.anyString())).andReturn(new SwiftCommonProcessHandlerTest());
        PowerMock.replay(ProxyServiceRegistry.class);
        PowerMock.replayAll();

        new SwiftCommonProcessHandler(new LocalInvokerCreator()).processResult(SwiftCommonProcessHandlerTest.class.getDeclaredMethod("testCommon", String.class), new Target[]{Target.ALL}, "test local");
        PowerMock.verifyAll();
    }

    private Invoker createInvoker() {
        return new Invoker() {

            @Override
            public URL getUrl() {
                return null;
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public void destroy() {

            }

            @Override
            public Class getInterface() {
                return null;
            }

            @Override
            public Result invoke(Invocation invocation) {
                Object proxy = new SwiftCommonProcessHandlerTest();
                try {
                    Method method = SwiftCommonProcessHandlerTest.class.getDeclaredMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    method.setAccessible(true);
                    method.invoke(proxy, invocation.getArguments());
                    // Generate by Mock Plugin
                    Result mockResult = PowerMock.createMock(Result.class);
                    EasyMock.expect(mockResult.recreate()).andReturn(null).anyTimes();
                    PowerMock.replayAll();

                    return mockResult;
                } catch (Throwable e) {
                    fail();
                    return null;
                }

            }
        };
    }

    public void testCommon(String test) {
        System.out.println(test);
    }
}