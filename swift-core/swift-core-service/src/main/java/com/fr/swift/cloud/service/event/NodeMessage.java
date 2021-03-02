package com.fr.swift.cloud.service.event;

import com.fr.swift.cloud.util.Strings;

import java.io.Serializable;

/**
 * @author Heng.J
 * @date 2020/11/3
 * @description
 * @since swift-1.2.0
 */
public class NodeMessage implements Serializable {

    private static final long serialVersionUID = 6312237404167456647L;

    private String clusterId;

    // 迁移任务记录 blockIndex
    private String messageInfo;

    public NodeMessage(String clusterId, String messageInfo) {
        this.clusterId = clusterId;
        this.messageInfo = messageInfo;
    }

    public static NodeMessage of(String clusterId, String messageInfo) {
        return new NodeMessage(clusterId, messageInfo);
    }

    public static NodeMessage of(String clusterId) {
        return of(clusterId, Strings.EMPTY);
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getMessageInfo() {
        return messageInfo;
    }
}
