package com.fr.swift.config.service;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/6
 * <p>
 * todo String改用更易读的对象替代
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
     * todo 墙裂要求return删掉的seg key
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
    Map<SourceKey, List<SegmentKey>> getAllSegments();

    Map<SourceKey, List<SegmentKey>> getOwnSegments();

    /**
     * 根据SourceKey获取Segment
     *
     * @param sourceKey
     * @return
     */
    List<SegmentKey> getSegmentByKey(String sourceKey);

    boolean containsSegment(SegmentKey segmentKey);

    /**
     * 尝试获取表最大segOrder的key
     *
     * @param tableKey
     * @return
     */
    SegmentKey tryAppendSegment(SourceKey tableKey, StoreType storeType);
}