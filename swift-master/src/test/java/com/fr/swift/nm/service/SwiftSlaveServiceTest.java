package com.fr.swift.nm.service;

import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import junit.framework.TestCase;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * This class created on 2019/1/8
 *
 * @author Lucifer
 * @description
 */
public class SwiftSlaveServiceTest extends TestCase {

    HeartBeatInfo heartBeatInfo;
    MasterService masterService;
    Collection<NodeState> collection;

    @Override
    public void setUp() throws Exception {

        ProxyFactory proxyFactory = PowerMockito.mock(ProxyFactory.class);
        masterService = PowerMockito.mock(MasterService.class);
        PowerMockito.when(proxyFactory.getProxy(MasterService.class)).thenReturn(masterService);
        ProxySelector.getInstance().switchFactory(proxyFactory);
        NodeState nodeState = PowerMockito.mock(NodeState.class);
        heartBeatInfo = PowerMockito.mock(HeartBeatInfo.class);
        PowerMockito.when(heartBeatInfo.getNodeId()).thenReturn("127.0.0.1:8080");
        PowerMockito.when(heartBeatInfo.getAddress()).thenReturn("127.0.0.1:8080");
        PowerMockito.when(heartBeatInfo.getHeartbeatTime()).thenReturn(new Date(System.currentTimeMillis() - 100000L));
        PowerMockito.when(nodeState.getHeartBeatInfo()).thenReturn(heartBeatInfo);
        PowerMockito.when(nodeState.getNodeType()).thenReturn(NodeType.DELAY);
        collection = new ArrayList<NodeState>(Collections.singletonList(nodeState));
    }

    public void testSendHeartBeat() throws Exception {
        NodeContainer.addAll(collection);
        SwiftSlaveService slaveService = new SwiftSlaveService();
        slaveService.sendHeartBeat(heartBeatInfo);
        Mockito.verify(masterService).receiveHeartBeat(heartBeatInfo);
    }

    public void testSyncNodeStates() throws Exception {
        NodeContainer.removeNodeStates();
        assertTrue(NodeContainer.getAllNodeStates().isEmpty());
        PowerMockito.when(masterService.pullNodeStates()).thenReturn(collection);
        SwiftSlaveService slaveService = new SwiftSlaveService();
        slaveService.syncNodeStates();
        assertEquals(NodeContainer.getAllNodeStates().size(), 1);
    }
}
