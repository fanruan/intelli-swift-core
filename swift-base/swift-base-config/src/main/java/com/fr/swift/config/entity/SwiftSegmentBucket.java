package com.fr.swift.config.entity;

import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        this.bucketMap = new ConcurrentHashMap<>();
        this.bucketIndexMap = new ConcurrentHashMap<>();
    }

    public void put(Integer key, SegmentKey value) {
        bucketMap.computeIfAbsent(key, n -> new ArrayList<>()).add(value);
        getBucketIndexMap().put(value, key);
    }

    public void remove(SegmentKey value) {
        Integer removedKey = bucketIndexMap.remove(value);
        if (removedKey != null) {
            bucketMap.computeIfAbsent(removedKey, n -> new ArrayList<>()).remove(value);
        }
    }

    public Map<Integer, List<SegmentKey>> getBucketMap() {
        return bucketMap;
    }

    public Map<SegmentKey, Integer> getBucketIndexMap() {
        return bucketIndexMap;
    }

}
