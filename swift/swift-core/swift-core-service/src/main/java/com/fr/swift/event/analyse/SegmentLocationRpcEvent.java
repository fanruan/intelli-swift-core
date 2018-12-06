package com.fr.swift.event.analyse;

import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.structure.Pair;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/20
 */
public class SegmentLocationRpcEvent extends AbstractAnalyseRpcEvent<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> implements Serializable {

    private static final long serialVersionUID = 8562422302509459743L;
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
