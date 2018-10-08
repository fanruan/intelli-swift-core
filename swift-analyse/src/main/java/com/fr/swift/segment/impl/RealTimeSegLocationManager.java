package com.fr.swift.segment.impl;

import com.fr.swift.segment.SegmentDestination;

/**
 * @author yee
 * @date 2018/10/7
 */
public class RealTimeSegLocationManager extends AbstractSegmentLocationManager {
    @Override
    protected void calculateRemoteSegment(String sourceKey, SegmentDestination destination) {
        if (!calculateContains(remoteSegments, sourceKey, destination)) {
            remoteSegments.get(sourceKey).get(destination.getSegmentId()).getSpareNodes().add(destination.getClusterId());
        }
    }
}
