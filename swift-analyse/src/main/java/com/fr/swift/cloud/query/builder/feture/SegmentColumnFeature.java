package com.fr.swift.cloud.query.builder.feture;

import com.fr.swift.cloud.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.cloud.segment.Segment;

import java.util.List;

/**
 * @author xiqiu
 * @date 2021/1/27
 * @description 缓存用，避免重复拿
 * @since swift-1.2.0
 */
public class SegmentColumnFeature {
    private BaseFeature<Object> start;
    private BaseFeature<Object> end;
    private BaseFeature<Integer> dictSize;
    private BaseFeature<Integer> nullCount;
    private BaseFeature<List<String>> likeValue;

    public SegmentColumnFeature(Segment segment, SwiftDetailFilterInfo detailFilterInfo) {
        this.start = new MinFeature(segment, detailFilterInfo);
        this.end = new MaxFeature(segment, detailFilterInfo);
        this.dictSize = new DictSizeFeature(segment, detailFilterInfo);
        this.nullCount = new NullCountFeature(segment, detailFilterInfo);
        this.likeValue = new LikeFeature(segment, detailFilterInfo);
    }

    public Object getStart() {
        return this.start.getValue();
    }

    public Object getEnd() {
        return this.end.getValue();
    }

    public Integer getDictSize() {
        return this.dictSize.getValue();
    }

    public Integer getNullCount() {
        return this.nullCount.getValue();
    }

    public List<String> getLikeValue() {
        return this.likeValue.getValue();
    }
}
