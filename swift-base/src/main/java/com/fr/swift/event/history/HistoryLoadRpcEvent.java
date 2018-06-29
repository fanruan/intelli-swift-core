package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;

/**
 * @author yee
 * @date 2018/6/8
 */
public class HistoryLoadRpcEvent extends AbstractHistoryRpcEvent<String> {

    private static final long serialVersionUID = 5999241318201878252L;

    private String sourceKey;

    public HistoryLoadRpcEvent(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public HistoryLoadRpcEvent() {
    }

    @Override
    public Event subEvent() {
        return Event.LOAD;
    }

    @Override
    public String getContent() {
        return sourceKey;
    }
}
