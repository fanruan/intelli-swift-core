package com.fr.swift.config.service;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/7
 */
public interface SwiftClusterSegmentService extends SwiftSegmentService {
    Map<SourceKey, List<SegmentKey>> getOwnSegments(String clusterId);

    boolean updateSegmentTable(Map<String, Set<SegmentKey>> segmentTable);

    void checkOldConfig();
}
