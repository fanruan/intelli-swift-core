package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/8
 */
public class HistoryLoadRpcEvent extends AbstractHistoryRpcEvent<List<SegmentKey>> {


    private static final long serialVersionUID = 5999241318201878252L;
    private List<SegmentKey> segmentKeys;

    public HistoryLoadRpcEvent(List<SegmentKey> segmentKeys) {
        this.segmentKeys = segmentKeys;
    }

    @Override
    public Event subEvent() {
        return Event.LOAD;
    }

    @Override
    public List<SegmentKey> getContent() {
        return segmentKeys;
    }
}
