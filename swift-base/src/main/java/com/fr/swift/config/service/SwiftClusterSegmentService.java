package com.fr.swift.config.service;

import com.fr.swift.segment.SegmentKey;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/7
 */
public interface SwiftClusterSegmentService extends SwiftSegmentService {
    Map<String, List<SegmentKey>> getOwnSegments();
}
