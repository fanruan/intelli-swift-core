package com.fr.swift.container;

import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
public class NodeContainerTest extends TestCase {

    public void testNodeContainer() {
        HeartBeatInfo heartBeatInfo1 = EasyMock.createMock(HeartBeatInfo.class);
        EasyMock.expect(heartBeatInfo1.getNodeId()).andReturn("heartBeatInfo1").anyTimes();
        HeartBeatInfo heartBeatInfo2 = EasyMock.createMock(HeartBeatInfo.class);
        EasyMock.expect(heartBeatInfo2.getNodeId()).andReturn("heartBeatInfo2").anyTimes();
        HeartBeatInfo heartBeatInfo3 = EasyMock.createMock(HeartBeatInfo.class);
        EasyMock.expect(heartBeatInfo3.getNodeId()).andReturn("heartBeatInfo3").anyTimes();


        EasyMock.replay(heartBeatInfo1, heartBeatInfo2, heartBeatInfo3);
        assertNotNull(NodeContainer.getInstance());
        assertTrue(NodeContainer.getAllNodeStates().isEmpty());
        List<NodeState> stateList = new ArrayList<NodeState>();
        stateList.add(new NodeState(heartBeatInfo1, NodeType.ONLINE));
        stateList.add(new NodeState(heartBeatInfo2, NodeType.ONLINE));
        stateList.add(new NodeState(heartBeatInfo3, NodeType.ONLINE));
        NodeContainer.addAll(stateList);
        assertEquals(NodeContainer.getAllNodeStates().size(), 3);
        assertNotNull(NodeContainer.getNode("heartBeatInfo1"));
        assertTrue(NodeContainer.containNode("heartBeatInfo1"));
        assertTrue(NodeContainer.isAvailable("heartBeatInfo1"));
        assertFalse(NodeContainer.isAvailable("heartBeatInfo4"));

        NodeContainer.updateNodeState(new NodeState(heartBeatInfo1, NodeType.OFFLINE));
        assertFalse(NodeContainer.isAvailable("heartBeatInfo1"));

        NodeContainer.removeNodeStates();
        assertTrue(NodeContainer.getAllNodeStates().isEmpty());
        assertFalse(NodeContainer.isAvailable("heartBeatInfo1"));
    }
}
