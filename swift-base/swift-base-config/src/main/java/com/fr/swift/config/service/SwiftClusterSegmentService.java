package com.fr.swift.config.service;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/7
 */
public interface SwiftClusterSegmentService extends SwiftSegmentService {
    Map<String, List<SegmentKey>> getOwnSegments(String clusterId);


    Map<String, List<SegmentKey>> getClusterSegments();

    boolean updateSegmentTable(Map<String, List<Pair<String, String>>> segmentTable);

    void checkOldConfig();

    void setClusterId(String clusterId);

}
