package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

/**
 * @author anchore
 * @date 2018/12/19
 */
public class HistoryHashSourceAlloter extends BaseHashSourceAlloter {

    public HistoryHashSourceAlloter(SourceKey tableKey, HashAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(HashRowInfo rowInfo) {
        int virtualOrder = super.getLogicOrder(rowInfo);
        return virtualOrder;
    }

    @Override
    protected SegmentState getInsertableSeg(int virtualOrder) {
        SegmentKey segKey = segmentService.tryAppendSegment(tableKey, StoreType.FINE_IO);
        SwiftSegmentBucketElement bucketElement = new SwiftSegmentBucketElement(tableKey, virtualOrder, segKey.getId());
        bucketService.save(bucketElement);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType());
        return new SegmentState(segInfo);
    }
}