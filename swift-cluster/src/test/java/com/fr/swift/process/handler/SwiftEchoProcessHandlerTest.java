package com.fr.swift.process.handler;

import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/4/2019
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftEchoProcessHandler.class, ClusterSwiftServerService.class})
@ProxyService(SwiftEchoProcessHandlerTest.class)
public class SwiftEchoProcessHandlerTest {

    InvokerCreator invokerCreator;
    URL url;
    SwiftService service;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ClusterSwiftServerService.class);

        ProxyServiceRegistry.get().registerService(new SwiftEchoProcessHandlerTest());
        invokerCreator = PowerMockito.mock(InvokerCreator.class);
        url = PowerMockito.mock(URL.class);
        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftEchoProcessHandlerTest.class.getName()), SwiftEchoProcessHandlerTest.class, null, true);
        PowerMockito.when(invokerCreator.createSyncInvoker(SwiftEchoProcessHandlerTest.class, url)).thenReturn(invoker);

        final ClusterEntity entity = PowerMockito.mock(ClusterEntity.class);
        ClusterSwiftServerService swiftServerService = PowerMockito.mock(ClusterSwiftServerService.class);
        PowerMockito.when(ClusterSwiftServerService.getInstance()).thenReturn(swiftServerService);
        PowerMockito.when(swiftServerService.getClusterEntityByService((ServiceType) ArgumentMatchers.any())).thenReturn(new HashMap<String, ClusterEntity>() {
            {
                put("service", entity);
            }
        });

        Destination destination = PowerMockito.mock(Destination.class);
        PowerMockito.when(url.getDestination()).thenReturn(destination);
        PowerMockito.when(destination.getId()).thenReturn("SwiftEchoProcessHandlerTest");

        PowerMockito.when(entity.getUrl()).thenReturn(url);
        service = PowerMockito.mock(SwiftService.class);
    }

    @Test
    public void testProcessUrl() {
        SwiftEchoProcessHandler handler = new SwiftEchoProcessHandler(invokerCreator);
        Set<URL> urls = handler.processUrl(null, service);
        Assert.assertTrue(1 == urls.size());
        Assert.assertEquals(urls.iterator().next().getDestination().getId(), "SwiftEchoProcessHandlerTest");
    }

    @Test
    public void testProcessResult() throws Throwable {
        SwiftEchoProcessHandler handler = PowerMockito.spy(new SwiftEchoProcessHandler(invokerCreator));
        Destination destination = PowerMockito.mock(Destination.class);
        PowerMockito.when(url.getDestination()).thenReturn(destination);
        PowerMockito.when(destination.getId()).thenReturn("LOCAL");
        Target[] targets = new Target[]{Target.ALL};
        PowerMockito.doReturn(Collections.singleton(url)).when(handler, "processUrl", targets, service);
        Set<String> resultSet = (Set<String>) handler.processResult(
                SwiftEchoProcessHandlerTest.class.getMethod("getId", SwiftService.class), targets, service);
        Assert.assertEquals(resultSet.iterator().next(), "127.0.0.1:7000");
    }

    public Set<String> getId(SwiftService service) {
        return Collections.singleton("127.0.0.1:7000");
    }
}

