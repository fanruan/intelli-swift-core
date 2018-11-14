package com.fr.swift.segment.impl;

import com.fr.swift.segment.SegmentDestination;

/**
 * @author yee
 * @date 2018/10/7
 */
public class RealTimeSegLocationManager extends AbstractSegmentLocationManager {
    @Override
    protected void calculateRemoteSegment(String sourceKey, SegmentDestination destination) {
        String key = destination.getSegmentId() + destination.getClusterId();
        if (!remoteSegments.get(sourceKey).containsKey(key)) {
            remoteSegments.get(sourceKey).put(key, destination);
        }
    }
}
