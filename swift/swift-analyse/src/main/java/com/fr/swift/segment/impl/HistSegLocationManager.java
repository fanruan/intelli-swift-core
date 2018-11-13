package com.fr.swift.segment.impl;

import com.fr.swift.segment.SegmentDestination;

/**
 * @author yee
 * @date 2018/6/13
 */
public class HistSegLocationManager extends AbstractSegmentLocationManager {


    @Override
    protected void calculateRemoteSegment(String sourceKey, SegmentDestination destination) {
        if (localSegments.get(sourceKey).containsKey(destination.getSegmentId())) {
            localSegments.get(sourceKey).get(destination.getSegmentId()).getSpareNodes().add(destination.getClusterId());
        } else if (!calculateContains(remoteSegments, sourceKey, destination)) {
            remoteSegments.get(sourceKey).get(destination.getSegmentId()).getSpareNodes().add(destination.getClusterId());
        }
    }
}
