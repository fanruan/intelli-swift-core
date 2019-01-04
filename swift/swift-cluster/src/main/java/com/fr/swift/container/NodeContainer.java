package com.fr.swift.container;

import com.fr.swift.ClusterContainer;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;

import java.util.Collection;
import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description 节点心跳容器
 * @since Advanced FineBI 5.0
 * todo 心跳持久化和初始化
 */
public class NodeContainer extends ClusterContainer<NodeState> {

    private static final NodeContainer INSTANCE = new NodeContainer();

    private NodeContainer() {
    }

    public static NodeContainer getInstance() {
        return INSTANCE;
    }

    public static void updateNodeState(NodeState newNodeState) {
        NodeState oldNodeState = getInstance().get(newNodeState.getHeartBeatInfo().getNodeId());
        if (oldNodeState != null) {
            oldNodeState.setHeartBeatInfo(newNodeState.getHeartBeatInfo());
            oldNodeState.setNodeType(newNodeState.getNodeType());
        } else {
            getInstance().add(newNodeState.getHeartBeatInfo().getNodeId(), newNodeState);
        }
    }

    public static List<NodeState> getAllNodeStates() {
        return getInstance().getAllUseable();
    }

    public static void removeNodeStates() {
        getInstance().removeAll();
    }

    public static void addAll(Collection<NodeState> nodeStateCollection) {
        for (NodeState nodeState : nodeStateCollection) {
            getInstance().add(nodeState.getHeartBeatInfo().getNodeId(), nodeState);
        }
    }

    public static NodeState getNode(String id) {
        return getInstance().get(id);
    }

    public static boolean containNode(String id) {
        return getInstance().contains(id);
    }

    public static boolean isAvailable(String id) {
        NodeState nodeState = getInstance().get(id);
        if (nodeState == null) {
            return false;
        }
        if (nodeState.getNodeType() == NodeType.ONLINE) {
            return true;
        }
        return false;
    }
}
