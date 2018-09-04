package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;

/**
 * @author yee
 * @date 2018/9/4
 */
public class NodeStartedEvent extends AbstractGlobalRpcEvent<String> {

    private String clusterId;

    public NodeStartedEvent(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public Event subEvent() {
        return Event.NODE_STARTED;
    }

    @Override
    public String getContent() {
        return clusterId;
    }
}
