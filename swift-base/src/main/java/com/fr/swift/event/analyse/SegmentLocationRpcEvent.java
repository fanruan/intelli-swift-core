package com.fr.swift.event.analyse;

import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;

/**
 * @author yee
 * @date 2018/6/20
 */
public class SegmentLocationRpcEvent extends AbstractAnalyseRpcEvent<SegmentLocationInfo> {

    private SegmentLocationInfo info;

    public SegmentLocationRpcEvent(SegmentLocationInfo info) {
        this.info = info;
    }

    @Override
    public Event subEvent() {
        return Event.SEGMENT_LOCATION;
    }

    @Override
    public SegmentLocationInfo getContent() {
        return info;
    }
}
