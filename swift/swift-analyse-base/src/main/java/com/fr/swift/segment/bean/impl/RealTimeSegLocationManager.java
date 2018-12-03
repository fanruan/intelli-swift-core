package com.fr.swift.segment.bean.impl;

import com.fr.swift.segment.bean.SegmentDestination;

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
