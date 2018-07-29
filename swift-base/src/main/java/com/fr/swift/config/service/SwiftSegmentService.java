package com.fr.swift.config.service;

import com.fr.swift.segment.SegmentKey;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/6
 */
public interface SwiftSegmentService extends ConfigService<SegmentKey> {
    /**
     * Segment
     *
     * @param segments
     * @return
     */
    boolean addSegments(List<SegmentKey> segments);

    /**
     * 批量删除Segment
     *
     * @param sourceKey
     * @return
     */
    boolean removeSegments(String... sourceKey);

    boolean removeSegments(List<SegmentKey> segmentKeys);

    /**
     * @param segments
     * @return
     */
    boolean updateSegments(String sourceKey, List<SegmentKey> segments);

    /**
     * 获取所有Segment
     *
     * @return
     */
    Map<String, List<SegmentKey>> getAllSegments();

    Map<String, List<SegmentKey>> getAllRealTimeSegments();

    /**
     * 根据SourceKey获取Segment
     *
     * @param sourceKey
     * @return
     */
    List<SegmentKey> getSegmentByKey(String sourceKey);

    boolean containsSegment(SegmentKey segmentKey);
}