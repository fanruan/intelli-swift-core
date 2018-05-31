package com.fr.swift.service;

import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/13.
 */
public class SegmentLocationProvider {
    private static SegmentLocationProvider ourInstance = new SegmentLocationProvider();

    public static SegmentLocationProvider getInstance() {
        return ourInstance;
    }

    private SegmentLocationManager manager;

    private SegmentLocationProvider() {
    }

    /**
     * 给AnalysisService调用并更新segment location信息
     *
     * @param locationInfo
     */
    void updateSegmentInfo(SegmentLocationInfo locationInfo) {
        manager.updateSegmentInfo(locationInfo);
    }

    public List<URI> getSegmentLocationURI(SourceKey table) {
        // TODO: 2018/5/30
        return new ArrayList<URI>();
    }
}
