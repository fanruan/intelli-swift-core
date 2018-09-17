package com.fr.swift.event.realtime;

import com.fr.swift.event.base.AbstractRealTimeRpcEvent;

/**
 * @author yee
 * @date 2018/6/8
 */
public class RealTimeMergeRpcEvent extends AbstractRealTimeRpcEvent<Void> {

    private static final long serialVersionUID = 8404690845632795949L;

    @Override
    public Event subEvent() {
        return Event.MERGE;
    }

    @Override
    public Void getContent() {
        return null;
    }
}
