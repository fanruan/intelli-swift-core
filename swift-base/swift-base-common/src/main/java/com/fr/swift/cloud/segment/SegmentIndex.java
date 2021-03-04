package com.fr.swift.cloud.segment;

import java.util.Collection;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/8
 */
public interface SegmentIndex<T> {

    void addIndex(T key, SegmentKey segmentKey);

    void transferIndex(SegmentKey oldSeg, SegmentKey newSeg);

    void removeIndex(T key, SegmentKey segmentKey);

    void removeSegments(SegmentKey... segmentKeys);

    Collection<SegmentKey> getSegments(T key);

}
