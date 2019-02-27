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

    /**
     * 用于生成一个新块的碎片块集合
     *
     * @param segments 一组待合并的块
     * @param allShow  每个块对应的allShow
     */
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