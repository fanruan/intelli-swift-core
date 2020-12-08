package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentSource;
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
        List<Object> keys = new ArrayList<>();
        int[] fieldIndexes = rule.getFieldIndexes();
        for (int fieldIndex : fieldIndexes) {
            keys.add(rowInfo.getRow().getValue(fieldIndex));
        }
        return rule.getHashFunction().indexOf(keys);
    }

    @Override
    protected SegmentState getInsertableSeg(int virtualOrder) {
        SegmentKey segKey = swiftSegmentService.tryAppendSegment(tableKey, StoreType.FINE_IO, SegmentSource.CREATED, rule.getCubePath(virtualOrder));
        SwiftSegmentBucketElement bucketElement = new SwiftSegmentBucketElement(tableKey, virtualOrder, segKey.getId());
        swiftSegmentService.saveBucket(bucketElement);

        SwiftLoggers.getLogger().debug("importing, append new seg {} in bucket {}", segKey, virtualOrder);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType(), segKey.getSegmentUri());
        return new SegmentState(segInfo);
    }
}