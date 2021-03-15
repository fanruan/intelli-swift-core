package com.fr.swift.cloud.executor.task.bean;

import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.source.SourceKey;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Heng.J
 * @date 2020/12/2
 * @description
 * @since swift-1.2.0
 */
public class CollateBean {

    private SourceKey sourceKey;

    private Set<String> segmentUris;

    private List<String> segmentIds;

    public CollateBean() {
    }

    public CollateBean(SourceKey sourceKey, List<SegmentKey> segmentKeys) {
        this.sourceKey = sourceKey;
        this.segmentUris = segmentKeys.stream().map(SegmentKey::getSegmentUri).collect(Collectors.toSet());
        this.segmentIds = segmentKeys.stream().map(SegmentKey::getId).collect(Collectors.toList());
    }

    public static CollateBean of(SourceKey sourceKey, List<SegmentKey> segmentKeys) {
        return new CollateBean(sourceKey, segmentKeys);
    }

    public SourceKey getSourceKey() {
        return sourceKey;
    }

    public Set<String> getSegmentUris() {
        return segmentUris;
    }

    public List<String> getSegmentIds() {
        return segmentIds;
    }

    @Override
    public String toString() {
        return "CollateBean{" +
                "sourceKey=" + getSourceKey() +
                ", segmentUris=" + getSegmentUris() +
                ", segmentIds=" + getSegmentIds() +
                '}';
    }
}
