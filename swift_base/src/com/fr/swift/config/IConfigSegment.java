package com.fr.swift.config;

import java.util.Map;

/**
 * @author yee
 * @date 2018/3/9
 */
public interface IConfigSegment {
    Map<String, ISegmentKey> getSegments();

    void setSegments(Map<String, ISegmentKey> segments);

    String getSourceKey();

    void setSourceKey(String sourceKey);

    void addOrUpdateSegment(ISegmentKey segmentKey);
}
