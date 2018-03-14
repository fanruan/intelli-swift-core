package com.fr.swift.config;

import java.util.List;

/**
 * @author yee
 * @date 2018/3/9
 */
public interface ISegment {
    List<ISegmentKey> getSegments();

    void setSegments(List<ISegmentKey> segments);

    String getSourceKey();

    void setSourceKey(String sourceKey);
}
