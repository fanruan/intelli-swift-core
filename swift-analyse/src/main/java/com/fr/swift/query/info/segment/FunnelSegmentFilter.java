package com.fr.swift.query.info.segment;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.segment.ReadonlyMultiSegment;
import com.fr.swift.segment.Segment;
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
    public List<Segment> filterSegment(Set<Integer> virtualOrders) {
        List<Segment> funnelSegmentList = new ArrayList<Segment>();
        for (Integer key : virtualOrders) {
            List<SegmentKey> segmentKeyList = bucketMap.get(key);
            if (segmentKeyList.size() == 1) {
                SegmentKey segmentKey = segmentKeyList.get(0);
                funnelSegmentList.add(SEG_SVC.getSegment(segmentKey));
                continue;
            }
            List<Segment> segmentList = new ArrayList<Segment>();
            for (SegmentKey segmentKey : segmentKeyList) {
                segmentList.add(SEG_SVC.getSegment(segmentKey));
            }
            Segment funnelSegment = new ReadonlyMultiSegment(segmentList);
            funnelSegmentList.add(funnelSegment);
        }
        return funnelSegmentList;
    }


}
