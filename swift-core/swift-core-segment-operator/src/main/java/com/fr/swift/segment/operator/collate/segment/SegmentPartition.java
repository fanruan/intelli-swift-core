package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by lyon on 2019/2/21.
 */
public class SegmentPartition {
    private List<SegmentKey> segmentKeys;
    private List<Segment> segments;
    private List<ImmutableBitMap> allShow;

    /**
     * 用于生成一个新块的碎片块集合
     *
     * @param segmentKeys 一组待合并的块
     * @param allShow     每个块对应的allShow
     */
    @Deprecated
    public SegmentPartition(List<SegmentKey> segmentKeys, List<ImmutableBitMap> allShow) {
        this.allShow = allShow;
        this.segmentKeys = segmentKeys;
    }

    public SegmentPartition(List<SegmentKey> segmentKeys) {
        this.segmentKeys = segmentKeys;
        this.segments = segmentKeys.stream().map(SegmentUtils::newSegment).collect(Collectors.toList());
        this.allShow = segments.stream().map(Segment::getAllShowIndex).collect(Collectors.toList());
    }

    public List<SegmentKey> getSegmentKeys() {
        return segmentKeys;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public List<ImmutableBitMap> getAllShow() {
        return allShow;
    }

    public int getCardinality() {
        int count = 0;
        for (ImmutableBitMap as : allShow) {
            count += as.getCardinality();
        }
        return count;
    }

    /**
     * @return 返回碎片块中最新的块的创建时间
     */
    public Date getCreateTime() {
        return segmentKeys.stream().map(SegmentKey::getCreateTime).max(Comparator.comparing(Date::getTime)).orElse(new Date());
    }

    /**
     * @return 返回碎片块中被访问时间最近的被访问时间
     */
    public Date getVisitedTime() {
        return segmentKeys.stream().map(SegmentKey::getVisitedTime).filter(Objects::nonNull)
                .max(Comparator.comparing(Date::getTime)).orElse(null);
    }

}