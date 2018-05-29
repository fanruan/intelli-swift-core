package com.fr.swift.service;

import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.Set;

/**
 * Created by Lyon on 2018/5/29.
 */
public interface SegmentLocationManager {

    Set<URI> getSegmentLocationURI(SourceKey table);

    void updateSegmentInfo(SegmentLocationInfo segmentInfo);
}
