package com.fr.swift.heart;

import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.property.SwiftProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * This class created on 2018/7/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class HeartBeatInfo implements Serializable {
    private static final long serialVersionUID = 1837673620203944766L;

    private Date heartbeatTime;
    private String nodeId;
    private ServerCurrentStatus serverCurrentStatus;
    private String address;
    private Map<String, Object> attachment;
    private transient SwiftProperty swiftProperty = SwiftProperty.getProperty();

    public HeartBeatInfo() {
        this.serverCurrentStatus = new ServerCurrentStatus(swiftProperty.getServerAddress());
        this.nodeId = serverCurrentStatus.getClusterId();
        this.heartbeatTime = new Date();
        this.address = swiftProperty.getServerAddress();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(Date heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public ServerCurrentStatus getServerCurrentStatus() {
        return serverCurrentStatus;
    }

    public void setServerCurrentStatus(ServerCurrentStatus serverCurrentStatus) {
        this.serverCurrentStatus = serverCurrentStatus;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    public String getNodeName() {
        return "{NodeId:" + nodeId + ",address:" + address + "}";
    }

    @Override
    public String toString() {
        return "HeartBeatInfo{" +
                "heartbeatTime=" + heartbeatTime +
                ", nodeId='" + nodeId + '\'' +
                ", address='" + address + '\'' +
                ", serverCurrentStatus=" + serverCurrentStatus.toString() +
                '}';
    }
}
