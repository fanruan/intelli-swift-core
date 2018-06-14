package com.fr.swift.segment;

import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * Created by Lyon on 2018/5/29.
 */
public interface SegmentLocationManager {

    List<SegmentDestination> getSegmentLocationURI(SourceKey table);

    void updateSegmentInfo(SegmentLocationInfo segmentInfo, SegmentLocationInfo.UpdateType updateType);
}
