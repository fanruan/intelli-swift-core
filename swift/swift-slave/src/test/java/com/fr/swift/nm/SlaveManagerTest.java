package com.fr.swift.nm;

import com.fr.swift.SwiftContext;
import com.fr.swift.nm.collector.SlaveHeartBeatCollect;
import com.fr.swift.node.SwiftClusterNodeManager;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.service.manager.ClusterServiceManager;
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
@PrepareForTest({SwiftContext.class, SwiftProperty.class, ServiceBeanFactory.class, SlaveManager.class, ClusterSelector.class})
public class SlaveManagerTest {

    @Mock
    SlaveHeartBeatCollect collect;
    @Mock
    SwiftContext swiftContext;
    @Mock
    SwiftProperty swiftProperty;
    @Mock
    SwiftService swiftService;
    @Mock
    ServiceManager serviceManager;
    @Mock
    ClusterServiceManager clusterServiceManager;
    @Mock
    ClusterSelector clusterSelector;
    @Mock
    SwiftClusterNodeManager swiftClusterNodeManager;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ServiceBeanFactory.class);
        PowerMockito.mockStatic(SwiftContext.class, SwiftProperty.class, SlaveManager.class, ClusterSelector.class);

        PowerMockito.whenNew(SlaveHeartBeatCollect.class).withAnyArguments().thenReturn(collect);
        Mockito.when(SwiftContext.get()).thenReturn(swiftContext);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(ServiceBeanFactory.getSwiftServiceByNames(Mockito.<String>anySet())).thenReturn(Collections.singletonList(swiftService));
        Mockito.when(swiftContext.getBean(ServiceManager.class)).thenReturn(serviceManager);
        Mockito.when(swiftContext.getBean(ClusterServiceManager.class)).thenReturn(clusterServiceManager);
        Mockito.when(ClusterSelector.getInstance()).thenReturn(clusterSelector);
        Mockito.when(clusterSelector.getFactory()).thenReturn(swiftClusterNodeManager);
    }

    @Test
    public void testSlaveManager() throws Exception {
        SlaveManager slaveManager = new SlaveManager();
        slaveManager.startUp();
        Mockito.verify(collect).startCollect();
        Mockito.verify(clusterServiceManager).registerService(Mockito.<SwiftService>anyList());
        slaveManager.shutDown();
        Mockito.verify(collect).stopCollect();
        Mockito.verify(clusterServiceManager).unregisterService(Mockito.<SwiftService>anyList());
    }
}
