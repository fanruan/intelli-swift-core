package com.fr.swift.cluster.service;

import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/21
 */
public final class SegmentLocationInfoContainer {
    private List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> locationInfo;

    private SegmentLocationInfoContainer() {
        locationInfo = new ArrayList<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>>();
    }

    public static SegmentLocationInfoContainer getContainer() {
        return SingletonHolder.CONTAINER;
    }

    public void clean() {
        locationInfo.clear();
    }

    public List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> getLocationInfo() {
        return locationInfo;
    }

    public void add(Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> infoPair) {
        this.locationInfo.add(infoPair);
    }

    private static class SingletonHolder {
        private static SegmentLocationInfoContainer CONTAINER = new SegmentLocationInfoContainer();
    }
}
