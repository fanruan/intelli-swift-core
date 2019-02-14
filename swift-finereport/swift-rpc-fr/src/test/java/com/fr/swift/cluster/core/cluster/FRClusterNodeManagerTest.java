package com.fr.swift.cluster.core.cluster;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.ClusterView;
import com.fr.swift.SwiftContext;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SwiftProperty.class, ClusterBridge.class})
public class FRClusterNodeManagerTest {

    @Mock
    ClusterView clusterView;
    @Mock
    ClusterNode currentNode;
    @Mock
    ClusterNode masterNode;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ClusterBridge.class);
        Mockito.when(ClusterBridge.getView()).thenReturn(clusterView);
        Mockito.when(clusterView.getCurrent()).thenReturn(currentNode);
        Mockito.when(currentNode.getID()).thenReturn("127.0.0.1:8080");
        Mockito.when(masterNode.getID()).thenReturn("127.0.0.1:8088");
    }

    @Test
    public void testFRClusterNodeManager() {
        assertNotNull(FRClusterNodeManager.getInstance());
        FRClusterNodeManager.getInstance().setCluster(true);

        assertNull(FRClusterNodeManager.getInstance().getMasterId());
        assertEquals(FRClusterNodeManager.getInstance().getCurrentId(), "127.0.0.1:8080");
        assertFalse(FRClusterNodeManager.getInstance().isMaster());

        FRClusterNodeManager.getInstance().setMasterNode(masterNode);
        assertEquals(FRClusterNodeManager.getInstance().getMasterNode(), masterNode);
        assertEquals(FRClusterNodeManager.getInstance().getCurrentNode(), currentNode);

        assertEquals(FRClusterNodeManager.getInstance().getMasterId(), "127.0.0.1:8088");
        assertEquals(FRClusterNodeManager.getInstance().getCurrentId(), "127.0.0.1:8080");

        assertFalse(FRClusterNodeManager.getInstance().isMaster());
        assertTrue(FRClusterNodeManager.getInstance().isCluster());

        FRClusterNodeManager.getInstance().setMasterNode(currentNode);
        assertTrue(FRClusterNodeManager.getInstance().isMaster());
    }

}
