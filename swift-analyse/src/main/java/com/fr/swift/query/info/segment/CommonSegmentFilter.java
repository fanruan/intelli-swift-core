package com.fr.swift.query.info.segment;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.query.info.SingleTableQueryInfo;
import com.fr.swift.segment.Segment;
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
    public List<Segment> filterSegment(Set<Integer> virtualOrders, SingleTableQueryInfo singleTableQueryInfo) {
        if (virtualOrders.contains(-1)) {
            return SEG_SVC.getSegmentsByIds(singleTableQueryInfo.getTable(), singleTableQueryInfo.getQuerySegment());
        }
        Map<Integer, List<SegmentKey>> bucketMap = segmentBucket.getBucketMap();
        List<Segment> segmentList = new ArrayList<Segment>();
        List<SegmentKey> segmentKeyList = new ArrayList<SegmentKey>();
        for (Integer hashKey : virtualOrders) {
            segmentKeyList.addAll(bucketMap.get(hashKey));
        }
        for (SegmentKey segmentKey : segmentKeyList) {
            segmentList.add(SEG_SVC.getSegment(segmentKey));
        }
        return segmentList;
    }
}
