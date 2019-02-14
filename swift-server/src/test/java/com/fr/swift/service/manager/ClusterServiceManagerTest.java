package com.fr.swift.service.manager;

import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.listener.RemoteSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.lang.reflect.Constructor;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftProperty.class, ProxySelector.class})
public class ClusterServiceManagerTest {

    @Mock
    SwiftProperty swiftProperty;
    @Mock
    SwiftService swiftService;
    @Mock
    ProxyFactory proxyFactory;
    @Mock
    ProxySelector proxySelector;
    @Mock
    RemoteSender remoteSender;


    String clusterId = "127.0.0.1:8080";

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ProxySelector.class);
        PowerMockito.mockStatic(SwiftProperty.class);
        Mockito.when(ProxySelector.getInstance()).thenReturn(proxySelector);
        Mockito.when(proxySelector.getFactory()).thenReturn(proxyFactory);
        Mockito.when(proxyFactory.getProxy(RemoteSender.class)).thenReturn(remoteSender);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(swiftProperty.getClusterId()).thenReturn(clusterId);
    }

    @Test
    public void testClusterServiceManager() throws Exception {
        Constructor constructor = ClusterServiceManager.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ClusterServiceManager manager = (ClusterServiceManager) constructor.newInstance();
        manager.registerService(swiftService);
        Mockito.verify(swiftService, Mockito.times(1)).setId(Mockito.anyString());
        Mockito.verify(remoteSender).registerService(swiftService);
        manager.unregisterService(swiftService);
        Mockito.verify(swiftService, Mockito.times(2)).setId(Mockito.anyString());
        Mockito.verify(remoteSender).unRegisterService(swiftService);
    }
}
