package com.fr.swift.heart;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
public class NodeStateTest extends TestCase {

    @Test
    public void testNodeState() {
        HeartBeatInfo heartBeatInfo = EasyMock.createMock(HeartBeatInfo.class);
        EasyMock.replay(heartBeatInfo);
        NodeState nodeState = new NodeState(heartBeatInfo, NodeType.ONLINE);
        assertEquals(nodeState.getHeartBeatInfo(), heartBeatInfo);
        assertEquals(nodeState.getNodeType(), NodeType.ONLINE);
        assertEquals(nodeState.toString(), "NodeState{heartBeatInfo=EasyMock for class com.fr.swift.heart.HeartBeatInfo, nodeType=ONLINE}");
        nodeState.setHeartBeatInfo(null);
        nodeState.setNodeType(NodeType.OFFLINE);
        assertEquals(nodeState.getHeartBeatInfo(), null);
        assertEquals(nodeState.getNodeType(), NodeType.OFFLINE);
    }
}
