package com.fr.swift.config;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/9
 */
public interface IConfigSegment {
    List<ISegmentKey> getSegments();

    void setSegments(List<ISegmentKey> segments);

    String getSourceKey();

    void setSourceKey(String sourceKey);

    void addSegment(ISegmentKey segmentKey);
}
