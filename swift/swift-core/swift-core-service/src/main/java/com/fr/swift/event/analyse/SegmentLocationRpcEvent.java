package com.fr.swift.event.analyse;

import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.structure.Pair;

/**
 * @author yee
 * @date 2018/6/20
 */
public class SegmentLocationRpcEvent extends AbstractAnalyseRpcEvent<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> {

    private static final long serialVersionUID = -6727606318749436582L;

    private Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> info;

    public SegmentLocationRpcEvent(SegmentLocationInfo.UpdateType updateType, SegmentLocationInfo info) {
        this.info = Pair.of(updateType, info);
    }

    @Override
    public Event subEvent() {
        return Event.SEGMENT_LOCATION;
    }

    @Override
    public Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> getContent() {
        return info;
    }
}
