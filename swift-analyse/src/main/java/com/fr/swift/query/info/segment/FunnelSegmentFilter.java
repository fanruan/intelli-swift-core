package com.fr.swift.query.info.segment;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.query.info.SingleTableQueryInfo;
import com.fr.swift.segment.SegmentKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Create by lifan on 2019-08-01 10:03
 */
public class FunnelSegmentFilter extends AbstractSegmentFilter {

    public FunnelSegmentFilter(SwiftTableAllotRule tableAllotRule, SwiftSegmentBucket segmentBucket) {
        super(tableAllotRule, segmentBucket);
    }

    @Override
    protected List<SegmentKey> filterSegment(Set<Integer> virtualOrders, SingleTableQueryInfo singleTableQueryInfo) {
        List<SegmentKey> funnelSegKeyList = new ArrayList<>();
        if (virtualOrders.contains(ALL_SEGMENT)) {
            virtualOrders = bucketMap.keySet();
        }
        for (Integer key : virtualOrders) {
            List<SegmentKey> segmentKeyList = bucketMap.get(key);
            if (segmentKeyList.size() == 1) {
                SegmentKey segmentKey = segmentKeyList.get(0);
                funnelSegKeyList.add(segmentKey);
                continue;
            }
        }
        return funnelSegKeyList;
    }
}
