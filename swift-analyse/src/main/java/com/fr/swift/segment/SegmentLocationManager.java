package com.fr.swift.segment;

import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/29.
 */
public interface SegmentLocationManager {

    List<SegmentDestination> getSegmentLocationURI(SourceKey table);

    void updateSegmentInfo(SegmentLocationInfo segmentInfo, SegmentLocationInfo.UpdateType updateType);

    Map<String, List<SegmentDestination>> getSegmentInfo();

    void removeTable(String clusterId, String sourceKey);

    void removeSegments(String clusterId, String sourceKey, List<String> segmentKeys);
}
