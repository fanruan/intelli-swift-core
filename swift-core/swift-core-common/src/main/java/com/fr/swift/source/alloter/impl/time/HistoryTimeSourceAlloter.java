package com.fr.swift.source.alloter.impl.time;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

/**
 * @author Marvin.Zhao
 * @date 2019/7/17
 */
public class HistoryTimeSourceAlloter extends BaseTimeSourceAlloter {

    public HistoryTimeSourceAlloter(SourceKey tableKey, TimeAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected int getLogicOrder(TimeRowInfo rowInfo) {
        int virtualOrder = super.getLogicOrder(rowInfo);
        return virtualOrder;
    }

    @Override
    protected SegmentState getInsertableSeg(int virtualOrder) {
        SegmentKey segKey = SEG_SVC.tryAppendSegment(tableKey, StoreType.FINE_IO);
        SwiftSegmentBucketElement bucketElement = new SwiftSegmentBucketElement(tableKey, virtualOrder, segKey.getId());
        BUCKET_SVC.saveElement(bucketElement);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType());
        return new SegmentState(segInfo);
    }
}