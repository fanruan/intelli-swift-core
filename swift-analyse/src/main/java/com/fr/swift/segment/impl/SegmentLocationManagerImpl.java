package com.fr.swift.segment.impl;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationManager;
import com.fr.swift.segment.rule.DestSelectRule;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/6/13
 */
public class SegmentLocationManagerImpl implements SegmentLocationManager {

    private Map<String, List<SegmentDestination>> segments;
    private DestSelectRule rule = DestSelectRule.DEFAULT;

    public SegmentLocationManagerImpl() {
        segments = new ConcurrentHashMap<String, List<SegmentDestination>>();
    }

    public void setRule(DestSelectRule rule) {
        this.rule = rule;
    }

    @Override
    public List<SegmentDestination> getSegmentLocationURI(SourceKey table) {
        List<SegmentDestination> destinations = segments.get(table.getId());
        if (null == destinations) {
            destinations = new ArrayList<SegmentDestination>();
        }
        destinations = rule.selectDestination(destinations);
        // 暂时先这么处理，，，，
        if (null == destinations || destinations.isEmpty()) {
            destinations = new ArrayList<SegmentDestination>();
            destinations.add(new SegmentDestinationImpl(URI.create(""), 0));
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
