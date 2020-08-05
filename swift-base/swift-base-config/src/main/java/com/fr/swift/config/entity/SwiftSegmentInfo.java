package com.fr.swift.config.entity;

import com.fr.swift.segment.SegmentInfo;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentVisited;

import java.util.Date;

/**
 * @author Moira
 * @date 2020/8/5
 * @description
 * @since swift-1.2.0
 */
public class SwiftSegmentInfo implements SegmentInfo {
    private SegmentKey segmentKey;
    private SegmentVisited segmentVisited;

    public SwiftSegmentInfo(SegmentKey segmentKey, SegmentVisited segmentVisited) {
        this.segmentKey = segmentKey;
        this.segmentVisited = segmentVisited;
    }

    public SegmentKey getSwiftSegmentEntity() {
        return segmentKey;
    }

    public SegmentVisited getSwiftSegmentVisitedEntity() {
        return segmentVisited;
    }

    @Override
    public SegmentKey getSegmentKey() {
        return segmentKey;
    }

    @Override
    public SegmentVisited getSegmentVisited() {
        return segmentVisited;
    }

    @Override
    public Date getCreateTime() {
        return segmentKey.getCreateTime();
    }

    @Override
    public Date getVisitedTime() {
        return segmentVisited.getVisitedTime();
    }

    @Override
    public int getVisits() {
        return segmentVisited.getVisits();
    }
}
