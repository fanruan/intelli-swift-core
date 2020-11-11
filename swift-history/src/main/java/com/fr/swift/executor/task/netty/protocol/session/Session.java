package com.fr.swift.executor.task.netty.protocol.session;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class Session {

    String nodeId;
    String nodeName;

    public Session() {
    }

    public Session(String nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return "Session{" +
                "nodeName='" + nodeName + "-" + nodeId + '\'' +
                '}';
    }
}
