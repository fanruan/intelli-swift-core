package com.fr.swift.cluster.core.cluster;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.ClusterNodeState;
import com.fr.cluster.core.ClusterView;
import com.fr.cluster.lock.ClusterLock;
import com.fr.cluster.lock.ClusterLockFactory;
import com.fr.swift.ClusterNodeService;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.property.SwiftProperty;
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
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SwiftProperty.class, ClusterBridge.class, FRClusterNodeService.class})
public class FRClusterNodeServiceTest {
    @Mock
    ClusterView clusterView;
    @Mock
    ClusterLockFactory factory;
    @Mock
    ClusterLock clusterLock;
    @Mock
    ClusterNode currentNode;
    @Mock
    ClusterNode masterNode;
    @Mock
    SwiftServiceInfoService serviceInfoService;
    @Mock
    SwiftContext swiftContext;
    @Mock
    SwiftProperty swiftProperty;
    @Mock
    SwiftServiceInfoBean dbSwiftServiceInfoBean;
    @Mock
    SwiftServiceInfoBean newSwiftServiceInfoBean;


    FRClusterNodeService frClusterNodeService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class, SwiftProperty.class, ClusterBridge.class);
        PowerMockito.whenNew(SwiftServiceInfoBean.class).withAnyArguments().thenReturn(newSwiftServiceInfoBean);

        Mockito.when(ClusterBridge.getView()).thenReturn(clusterView);
        Mockito.when(clusterView.getCurrent()).thenReturn(currentNode);
        Mockito.when(currentNode.getID()).thenReturn("127.0.0.1:8080");
        Mockito.when(masterNode.getID()).thenReturn("127.0.0.1:8088");

        Mockito.when(ClusterBridge.getLockFactory()).thenReturn(factory);
        Mockito.when(factory.get(FRClusterNodeService.class)).thenReturn(clusterLock);

        Mockito.when(SwiftContext.get()).thenReturn(swiftContext);
        Mockito.when(swiftContext.getBean(SwiftServiceInfoService.class)).thenReturn(serviceInfoService);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);

        Mockito.when(dbSwiftServiceInfoBean.getClusterId()).thenReturn("127.0.0.1:8088");

        Constructor constructor = FRClusterNodeService.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        frClusterNodeService = (FRClusterNodeService) constructor.newInstance();
    }

    /**
     * master为空，current就是master
     */
    @Test
    public void testMasterIsNull() {
        Mockito.when(serviceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE)).thenReturn(Collections.EMPTY_LIST);
        assertTrue(frClusterNodeService.competeMaster());
        Mockito.verify(serviceInfoService).getServiceInfoByService(ClusterNodeService.SERVICE);
        Mockito.verify(serviceInfoService).saveOrUpdate(newSwiftServiceInfoBean);
        Mockito.verify(swiftProperty).setMasterAddress(currentNode.getID());
        assertEquals(FRClusterNodeManager.getInstance().getMasterNode(), FRClusterNodeManager.getInstance().getCurrentNode());
        assertTrue(FRClusterNodeManager.getInstance().isMaster());
    }

    /**
     * AbstractSwiftServerService
     * master不为空，但是master挂了，current为master
     */
    @Test
    public void testMasterIsDead() {
        Mockito.when(serviceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE)).thenReturn(Collections.singletonList(dbSwiftServiceInfoBean));
        assertTrue(frClusterNodeService.competeMaster(masterNode));
        Mockito.verify(serviceInfoService).getServiceInfoByService(ClusterNodeService.SERVICE);
        Mockito.verify(serviceInfoService).saveOrUpdate(newSwiftServiceInfoBean);
        Mockito.verify(swiftProperty).setMasterAddress(currentNode.getID());
        assertEquals(FRClusterNodeManager.getInstance().getMasterNode(), FRClusterNodeManager.getInstance().getCurrentNode());
        assertTrue(FRClusterNodeManager.getInstance().isMaster());
    }

    /**
     * master不为空且存活，current为slave
     */
    @Test
    public void testMasterIdAlive() {
        Mockito.when(serviceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE)).thenReturn(Collections.singletonList(dbSwiftServiceInfoBean));
        Mockito.when(clusterView.getNodeById("127.0.0.1:8088")).thenReturn(masterNode);
        Mockito.when(masterNode.getState()).thenReturn(ClusterNodeState.Member);
        assertTrue(frClusterNodeService.competeMaster(null));
        Mockito.verify(serviceInfoService).getServiceInfoByService(ClusterNodeService.SERVICE);
        Mockito.verify(swiftProperty).setMasterAddress(masterNode.getID());
        assertNotEquals(FRClusterNodeManager.getInstance().getMasterNode(), FRClusterNodeManager.getInstance().getCurrentNode());
        assertFalse(FRClusterNodeManager.getInstance().isMaster());

    }
}
