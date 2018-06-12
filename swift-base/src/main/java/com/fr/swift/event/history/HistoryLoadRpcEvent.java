package com.fr.swift.event.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.segment.SegmentKey;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
public class HistoryLoadRpcEvent extends AbstractHistoryRpcEvent<Map<String, List<SegmentKey>>> {


    private static final long serialVersionUID = 5999241318201878252L;

    /**
     * sourceKey->segmentKeyList
     */
    private Map<String, List<SegmentKey>> segmentKeys;

    public HistoryLoadRpcEvent(Map<String, List<SegmentKey>> segmentKeys) {
        this.segmentKeys = segmentKeys;
    }

    @Override
    public Event subEvent() {
        return Event.LOAD;
    }

    @Override
    public Map<String, List<SegmentKey>> getContent() {
        return segmentKeys;
    }
}
