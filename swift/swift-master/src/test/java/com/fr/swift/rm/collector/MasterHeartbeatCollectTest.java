package com.fr.swift.rm.collector;

import com.fr.swift.SwiftContext;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.rm.service.SwiftMasterService;
import com.fr.swift.util.concurrent.SwiftExecutors;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
public class MasterHeartbeatCollectTest extends TestCase {
    SwiftMasterService swiftMasterService;

    @Override
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        SwiftContext context = PowerMockito.mock(SwiftContext.class);
        swiftMasterService = PowerMockito.mock(SwiftMasterService.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(context);
        PowerMockito.when(context.getBean(SwiftMasterService.class)).thenReturn(swiftMasterService);
    }

    public void testCollect() throws InterruptedException {
        PowerMockito.mockStatic(SwiftExecutors.class);
        MasterHeartbeatCollect collect = new MasterHeartbeatCollect();
        Thread thread = PowerMockito.mock(Thread.class);
        PowerMockito.when(SwiftExecutors.newThread(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyString())).thenReturn(thread);
        collect.startCollect();
        collect.stopCollect();
        Mockito.verify(thread).start();
        Mockito.verify(thread).interrupt();
    }

    public void testRun() throws InterruptedException {
        NodeState nodeState = PowerMockito.mock(NodeState.class);
        HeartBeatInfo heartBeatInfo = PowerMockito.mock(HeartBeatInfo.class);
        PowerMockito.when(heartBeatInfo.getNodeId()).thenReturn("127.0.0.1:8080");
        PowerMockito.when(heartBeatInfo.getAddress()).thenReturn("127.0.0.1:8080");
        PowerMockito.when(heartBeatInfo.getHeartbeatTime()).thenReturn(new Date(System.currentTimeMillis() - 100000L));
        PowerMockito.when(nodeState.getHeartBeatInfo()).thenReturn(heartBeatInfo);
        PowerMockito.when(nodeState.getNodeType()).thenReturn(NodeType.DELAY);
        PowerMockito.mockStatic(NodeContainer.class);
        PowerMockito.when(NodeContainer.getAllNodeStates()).thenReturn(Collections.singletonList(nodeState));
        ClusterSwiftServerService clusterSwiftServerService = PowerMockito.mock(ClusterSwiftServerService.class);
        PowerMockito.mockStatic(ClusterSwiftServerService.class);
        PowerMockito.when(ClusterSwiftServerService.getInstance()).thenReturn(clusterSwiftServerService);

        MasterHeartbeatCollect collect = new MasterHeartbeatCollect();
        collect.startCollect();
        Thread.sleep(1000l);

        Mockito.verify(clusterSwiftServerService).offline(Mockito.anyString());
        Mockito.verify(swiftMasterService).pushNodeStates();
        Mockito.verify(nodeState).setNodeType(NodeType.OFFLINE);
    }
}
