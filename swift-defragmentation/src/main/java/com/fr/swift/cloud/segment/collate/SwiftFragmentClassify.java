package com.fr.swift.cloud.segment.collate;

import com.fr.swift.cloud.config.entity.SwiftSegmentBucket;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.operator.collate.segment.LinePartitioner;
import com.fr.swift.cloud.segment.operator.collate.segment.Partitioner;
import com.fr.swift.cloud.segment.operator.collate.segment.SegmentPartition;
import com.fr.swift.cloud.source.alloter.AllotRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
public class SwiftFragmentClassify {

    private SwiftSegmentBucket segmentBucket;

    private AllotRule allotRule;

    private static final int LINE_VIRTUAL_INDEX = -1;

    public SwiftFragmentClassify(SwiftSegmentBucket segmentBucket, AllotRule allotRule) {
        this.segmentBucket = segmentBucket;
        this.allotRule = allotRule;
    }

    public Map<Integer, List<SegmentPartition>> classify(Collection<SegmentKey> segKeys) {
        Map<Integer, List<SegmentKey>> segKeyClassifyMap = new HashMap<>();
        Map<SegmentKey, Integer> bucketIndexMap = segmentBucket.getBucketIndexMap();
        for (SegmentKey segKey : segKeys) {
            Integer index = bucketIndexMap.get(segKey) == null ? LINE_VIRTUAL_INDEX : bucketIndexMap.get(segKey);
            segKeyClassifyMap.computeIfAbsent(index, n -> new ArrayList<>()).add(segKey);
        }
        Map<Integer, List<SegmentPartition>> itemMap = new HashMap<>();
        for (Map.Entry<Integer, List<SegmentKey>> classifyEntry : segKeyClassifyMap.entrySet()) {
            if (classifyEntry.getValue().size() >= SwiftFragmentFilter.FRAGMENT_NUMBER) {
                Partitioner partitioner = new LinePartitioner(allotRule.getCapacity());
                List<SegmentPartition> items = partitioner.partition(classifyEntry.getValue());
                itemMap.put(classifyEntry.getKey(), items);
            }
        }
        return itemMap;
    }
}
