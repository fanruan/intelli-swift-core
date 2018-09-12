package com.fr.swift.event.analyse;

import com.fr.swift.event.base.AbstractAnalyseRpcEvent;

/**
 * @author yee
 * @date 2018/9/4
 */
public class RequestSegLocationEvent extends AbstractAnalyseRpcEvent<String> {

    private String clusterId;

    public RequestSegLocationEvent(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public Event subEvent() {
        return Event.REQUEST_SEG_LOCATION;
    }

    @Override
    public String getContent() {
        return clusterId;
    }
}
