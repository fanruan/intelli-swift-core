package com.fr.swift.rm;

import com.fr.swift.ClusterNodeManager;
import com.fr.swift.SwiftContext;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.rm.collector.MasterHeartbeatCollect;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
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

import java.util.Collections;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SwiftProperty.class, MasterManager.class, ServiceBeanFactory.class
        , ClusterSelector.class, SwiftServiceListenerManager.class, ClusterSwiftServerService.class})
public class MasterManagerTest {

    @Mock
    MasterHeartbeatCollect collect;
    @Mock
    SwiftContext swiftContext;
    @Mock
    SwiftProperty swiftProperty;
    @Mock
    ServiceManager serviceManager;
    @Mock
    SwiftServiceInfoService serviceInfoService;
    @Mock
    SwiftService swiftService;
    @Mock
    ClusterSelector clusterSelector;
    @Mock
    ClusterNodeManager clusterNodeManager;
    @Mock
    SwiftServiceListenerManager swiftServiceListenerManager;
    @Mock
    ClusterSwiftServerService clusterSwiftServerService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ServiceBeanFactory.class);
        PowerMockito.mockStatic(SwiftContext.class, SwiftProperty.class, ClusterSelector.class
                , SwiftServiceListenerManager.class, ClusterSwiftServerService.class, MasterManager.class);
        PowerMockito.whenNew(MasterHeartbeatCollect.class).withAnyArguments().thenReturn(collect);
        Mockito.when(SwiftContext.get()).thenReturn(swiftContext);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(swiftContext.getBean(ServiceManager.class)).thenReturn(serviceManager);
        Mockito.when(swiftContext.getBean(SwiftServiceInfoService.class)).thenReturn(serviceInfoService);
        Mockito.when(swiftProperty.getMasterAddress()).thenReturn("127.0.0.1:8080");
        Mockito.when(swiftProperty.getSwiftServiceNames()).thenReturn(Collections.EMPTY_SET);
        Mockito.when(ServiceBeanFactory.getSwiftServiceByNames(Mockito.<String>anySet())).thenReturn(Collections.singletonList(swiftService));
        Mockito.when(ClusterSelector.getInstance()).thenReturn(clusterSelector);
        Mockito.when(clusterSelector.getFactory()).thenReturn(clusterNodeManager);
        Mockito.when(clusterNodeManager.getCurrentId()).thenReturn("127.0.0.1:8088");
        Mockito.when(SwiftServiceListenerManager.getInstance()).thenReturn(swiftServiceListenerManager);
        Mockito.when(ClusterSwiftServerService.getInstance()).thenReturn(clusterSwiftServerService);
    }

    @Test
    public void testMasterManager() throws Exception {
        MasterManager masterManager = new MasterManager();
        masterManager.startUp();
        Mockito.verify(clusterSwiftServerService).start();
        Mockito.verify(serviceManager).startUp();
        Mockito.verify(swiftService, Mockito.times(1)).setId("127.0.0.1:8088");
        Mockito.verify(collect).startCollect();
        Mockito.verify(swiftServiceListenerManager).registerService(swiftService);

        masterManager.shutDown();
        Mockito.verify(collect).stopCollect();
        Mockito.verify(swiftService, Mockito.times(2)).setId("127.0.0.1:8088");
        Mockito.verify(swiftServiceListenerManager).unRegisterService(swiftService);
    }
}
