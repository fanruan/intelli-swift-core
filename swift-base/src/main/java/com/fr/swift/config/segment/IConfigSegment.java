package com.fr.swift.config.segment;

import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/3/9
 */
public interface IConfigSegment {
    List<SegmentKey> getSegments();

    void setSegments(List<SegmentKey> segments);

    String getSourceKey();

    void setSourceKey(String sourceKey);

    void addSegment(SegmentKey segmentKey);
}
