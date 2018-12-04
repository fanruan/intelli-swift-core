package com.fr.swift.segment.bean.impl;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SourceKey;

/**
 * @author yee
 * @date 2018/10/7
 */
public class RealTimeSegLocationManager extends AbstractSegmentLocationManager {
    @Override
    protected void calculateRemoteSegment(SourceKey sourceKey, SegmentDestination destination) {
        String key = destination.getSegmentId() + destination.getClusterId();
        if (!remoteSegments.get(sourceKey).containsKey(key)) {
            remoteSegments.get(sourceKey).put(key, destination);
        }
    }
}
