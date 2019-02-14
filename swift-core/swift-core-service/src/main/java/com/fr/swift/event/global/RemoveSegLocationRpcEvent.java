package com.fr.swift.event.global;

import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;

/**
 * @author anchore
 * @date 2018/12/28
 */
public class RemoveSegLocationRpcEvent extends AbstractGlobalRpcEvent<SegmentLocationInfo> {

    private static final long serialVersionUID = 2039792547543772230L;

    private String clusterId;

    private SegmentLocationInfo locations;

    public RemoveSegLocationRpcEvent(String clusterId, SegmentLocationInfo locations) {
        this.clusterId = clusterId;
        this.locations = locations;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Override
    public Event subEvent() {
        return Event.REMOVE_SEG;
    }

    @Override
    public SegmentLocationInfo getContent() {
        return locations;
    }
}
