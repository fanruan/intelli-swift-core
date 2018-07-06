package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;

/**
 * @author yee
 * @date 2018/6/8
 */
public class HistoryLoadSegmentRpcEvent extends AbstractHistoryRpcEvent<String> {

    private static final long serialVersionUID = 5999241318201878252L;

    private String sourceKey;

    public HistoryLoadSegmentRpcEvent(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public HistoryLoadSegmentRpcEvent() {
    }

    @Override
    public Event subEvent() {
        return Event.LOAD_SEGMENT;
    }

    @Override
    public String getContent() {
        return sourceKey;
    }
}
