package com.fr.swift.query.info.segment;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.query.info.SingleTableQueryInfo;
import com.fr.swift.segment.SegmentKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Create by lifan on 2019-07-18 15:43
 */
public class CommonSegmentFilter extends AbstractSegmentFilter {

    public CommonSegmentFilter(SwiftTableAllotRule tableAllotRule, SwiftSegmentBucket segmentBucket) {
        super(tableAllotRule, segmentBucket);
    }

    @Override
    protected List<SegmentKey> filterSegment(Set<Integer> virtualOrders, SingleTableQueryInfo singleTableQueryInfo) {
        if (virtualOrders.contains(ALL_SEGMENT)) {
            return SEG_SVC.getSegmentKeysByIds(singleTableQueryInfo.getTable(), singleTableQueryInfo.getQuerySegment());
        }
        Map<Integer, List<SegmentKey>> bucketMap = segmentBucket.getBucketMap();
        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>();
        for (Integer hashKey : virtualOrders) {
            if (!bucketMap.containsKey(hashKey)) {
                continue;
            }
            segmentKeyList.addAll(bucketMap.get(hashKey));
        }
        return segmentKeyList;
    }
}
