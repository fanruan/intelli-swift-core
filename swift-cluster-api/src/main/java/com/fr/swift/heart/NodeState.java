package com.fr.swift.heart;

import java.io.Serializable;

/**
 * This class created on 2018/7/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class NodeState implements Serializable {

    private static final long serialVersionUID = 3930712723392870381L;
    private HeartBeatInfo heartBeatInfo;
    private NodeType nodeType;

    public NodeState(HeartBeatInfo heartBeatInfo, NodeType nodeType) {
        this.heartBeatInfo = heartBeatInfo;
        this.nodeType = nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void setHeartBeatInfo(HeartBeatInfo heartBeatInfo) {
        this.heartBeatInfo = heartBeatInfo;
    }

    public HeartBeatInfo getHeartBeatInfo() {
        return heartBeatInfo;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public String toString() {
        return "NodeState{" +
                "heartBeatInfo=" + heartBeatInfo +
                ", nodeType=" + nodeType +
                '}';
    }
}
