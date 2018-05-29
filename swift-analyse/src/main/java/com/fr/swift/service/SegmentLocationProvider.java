package com.fr.swift.service;

import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

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

    public Set<URI> getSegmentLocaltionURI(SourceKey table) {
        Set<URI> set = new HashSet<URI>();
        try {
            set.add(new URI(""));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return set;
    }
}
