package com.fr.swift.rm.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.util.concurrent.SwiftExecutors;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, SwiftExecutors.class, NodeContainer.class, ClusterSwiftServerService.class})
public class SwiftMasterServiceTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(SwiftContext.class);
        SwiftContext context = PowerMockito.mock(SwiftContext.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(context);
    }

    public void testSwiftMasterService() throws Exception {
        ProxyFactory proxyFactory = PowerMockito.mock(ProxyFactory.class);
        SlaveService slaveService = PowerMockito.mock(SlaveService.class);
        PowerMockito.when(proxyFactory.getProxy(SlaveService.class)).thenReturn(slaveService);
        ProxySelector.getInstance().switchFactory(proxyFactory);
        NodeState nodeState = PowerMockito.mock(NodeState.class);
        HeartBeatInfo heartBeatInfo = PowerMockito.mock(HeartBeatInfo.class);
        PowerMockito.when(heartBeatInfo.getNodeId()).thenReturn("127.0.0.1:8080");
        PowerMockito.when(heartBeatInfo.getAddress()).thenReturn("127.0.0.1:8080");
        PowerMockito.when(heartBeatInfo.getHeartbeatTime()).thenReturn(new Date(System.currentTimeMillis() - 100000L));
        PowerMockito.when(nodeState.getHeartBeatInfo()).thenReturn(heartBeatInfo);
        PowerMockito.when(nodeState.getNodeType()).thenReturn(NodeType.DELAY);
        Collection<NodeState> collection = new ArrayList<NodeState>(Collections.singletonList(nodeState));
        NodeContainer.addAll(collection);
        SwiftMasterService swiftMasterService = new SwiftMasterService();
        swiftMasterService.receiveHeartBeat(heartBeatInfo);
        Mockito.verify(slaveService).syncNodeStates(Mockito.<NodeState>anyCollection());
        assertEquals(swiftMasterService.pullNodeStates().size(), 1);
    }
}
