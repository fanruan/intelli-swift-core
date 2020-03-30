package com.fr.swift.config.entity;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
public class SwiftSegmentBucket {

    private String sourceKey;

    private Map<Integer, List<SegmentKey>> bucketMap;

    private Map<SegmentKey, Integer> bucketIndexMap;

    public SwiftSegmentBucket(SourceKey sourceKey) {
        this.sourceKey = sourceKey.getId();
        this.bucketMap = new HashMap<>();
        this.bucketIndexMap = new HashMap<>();
    }

    public void put(Integer key, SegmentKey value) {
        if (!bucketMap.containsKey(key)) {
            bucketMap.put(key, new ArrayList<>());
        }
        bucketMap.get(key).add(value);
        getBucketIndexMap().put(value, key);
    }

    public Map<Integer, List<SegmentKey>> getBucketMap() {
        return bucketMap;
    }

    public Map<SegmentKey, Integer> getBucketIndexMap() {
        return bucketIndexMap;
    }

}
