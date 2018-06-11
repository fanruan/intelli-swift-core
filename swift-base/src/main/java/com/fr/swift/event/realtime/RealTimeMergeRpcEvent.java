package com.fr.swift.event.realtime;

import com.fr.swift.event.base.AbstractRealTimeRpcEvent;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/8
 */
public class RealTimeMergeRpcEvent extends AbstractRealTimeRpcEvent<List<SourceKey>> {

    private List<SourceKey> sourceKeys;


    public RealTimeMergeRpcEvent(List<SourceKey> sourceKeys) {

    }

    @Override
    public Event subEvent() {
        return Event.MERGE;
    }

    @Override
    public List<SourceKey> getContent() {
        return sourceKeys;
    }
}
