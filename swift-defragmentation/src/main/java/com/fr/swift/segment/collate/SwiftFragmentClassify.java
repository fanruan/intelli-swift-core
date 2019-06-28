package com.fr.swift.segment.collate;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.segment.SegmentKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

    private static final int LINE_VIRTUAL_INDEX = -1;

    public SwiftFragmentClassify(SwiftSegmentBucket segmentBucket) {
        this.segmentBucket = segmentBucket;
    }

    public Map<Integer, List<SegmentKey>> classify(Collection<SegmentKey> segKeys) {
        Map<Integer, List<SegmentKey>> resultMap = new HashMap<Integer, List<SegmentKey>>();
        Map<SegmentKey, Integer> bucketIndexMap = segmentBucket.getBucketIndexMap();
        for (SegmentKey segKey : segKeys) {
            Integer index = bucketIndexMap.get(segKey);
            if (index == null) {
                index = LINE_VIRTUAL_INDEX;
            }
            if (!resultMap.containsKey(index)) {
                resultMap.put(index, new ArrayList<SegmentKey>());
            }
            resultMap.get(index).add(segKey);
        }
        Iterator<Integer> iterator = resultMap.keySet().iterator();
        while (iterator.hasNext()) {
            List<SegmentKey> segmentKeys = resultMap.get(iterator.next());
            if (segmentKeys.size() < SwiftFragmentFilter.FRAGMENT_NUMBER) {
                iterator.remove();
            }
        }
        return resultMap;
    }
}
