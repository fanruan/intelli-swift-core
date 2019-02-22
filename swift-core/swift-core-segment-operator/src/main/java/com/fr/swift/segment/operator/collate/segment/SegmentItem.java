package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
class SegmentItem {
    private List<Segment> segments;
    private List<ImmutableBitMap> allShow;

    public SegmentItem(List<Segment> segments, List<ImmutableBitMap> allShow) {
        this.segments = segments;
        this.allShow = allShow;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public List<ImmutableBitMap> getAllShow() {
        return allShow;
    }
}