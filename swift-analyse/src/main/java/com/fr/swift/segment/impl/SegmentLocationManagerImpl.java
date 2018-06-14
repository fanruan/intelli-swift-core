package com.fr.swift.segment.impl;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationManager;
import com.fr.swift.source.SourceKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/6/13
 */
public class SegmentLocationManagerImpl implements SegmentLocationManager {

    private Map<String, List<SegmentDestination>> segments;

    public SegmentLocationManagerImpl() {
        segments = new ConcurrentHashMap<String, List<SegmentDestination>>();
    }

    @Override
    public List<SegmentDestination> getSegmentLocationURI(SourceKey table) {
        List<SegmentDestination> destinations = segments.get(table.getId());
        if (null == destinations) {
            return Collections.emptyList();
        }
        return destinations;
    }

    @Override
    public void updateSegmentInfo(SegmentLocationInfo segmentInfo, SegmentLocationInfo.UpdateType updateType) {
        if (updateType == SegmentLocationInfo.UpdateType.ALL) {
            segments.clear();

        }
        segments.putAll(segmentInfo.getDestinations());
    }
}
