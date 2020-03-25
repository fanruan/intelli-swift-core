package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

import java.util.ArrayList;
import java.util.List;

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
        List keys = new ArrayList();
        for (Object fieldIndex : rule.getFieldIndexes()) {
            keys.add(rowInfo.getRow().getValue(((Number) fieldIndex).intValue()));
        }
        int index = rule.getHashFunction().indexOf(keys);
        return index;
    }

    @Override
    protected SegmentState getInsertableSeg(int virtualOrder) {
        SegmentKey segKey = SEG_SVC.tryAppendSegment(tableKey, StoreType.FINE_IO);
        SwiftSegmentBucketElement bucketElement = new SwiftSegmentBucketElement(tableKey, virtualOrder, segKey.getId());
        BUCKET_SVC.saveElement(bucketElement);
        SwiftLoggers.getLogger().debug("importing, append new seg {} in bucket {}", segKey, virtualOrder);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType());
        return new SegmentState(segInfo);
    }
}