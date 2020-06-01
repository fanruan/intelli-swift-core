package com.fr.swift.query.info.segment;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.segment.SegmentKey;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/5/7
 * @description
 * @since swift 1.1
 */
public class SegmentFuzzyBucket {

    protected final static int ALL_SEGMENT = -1;

    private Map<Integer, List<SegmentKey>> bucketMap;

    private Map<Integer, HashSet<Integer>> keyFuzzy = new HashMap<>();

    public SegmentFuzzyBucket(SwiftSegmentBucket segmentBucket) {
        this.bucketMap = segmentBucket.getBucketMap();
        init();
    }

    private void init() {
        Set<Integer> bucketKeys = bucketMap.keySet();
        for (Integer bucketKey : bucketKeys) {
            keyFuzzy.computeIfAbsent(bucketKey / 100, k -> new HashSet<>()).add(bucketKey);
            keyFuzzy.computeIfAbsent(bucketKey % 100, k -> new HashSet<>()).add(bucketKey);
        }
    }

    public Set<Integer> getIncludedKey(Set<Integer> hashValueSet) {
        if (hashValueSet.contains(ALL_SEGMENT)) {
            return bucketMap.keySet();
        }
        Set<Integer> resultSet = new HashSet();
        hashValueSet.stream().filter(hashValue -> keyFuzzy.containsKey(hashValue))
                .map(hashValue -> keyFuzzy.get(hashValue)).forEach(resultSet::addAll);
        return resultSet;
    }

    public Set<Integer> getNotIncludedKey(Set<Integer> hashValueSet) {
        if (hashValueSet.contains(ALL_SEGMENT)) {
            return bucketMap.keySet();
        }
        Set<Integer> resultSet = new HashSet<>(bucketMap.keySet());
        resultSet.removeAll(hashValueSet);
        return resultSet;
    }
}
