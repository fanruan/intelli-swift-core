package com.fr.swift.source.alloter.impl.hash;

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
        return Math.abs(rowInfo.getRow().getValue(rule.getFieldIndex()).hashCode() % rule.getSegCount());
    }

    @Override
    protected SegmentState getInsertableSeg() {
        // todo hash出的seg key可能还要写入此seg key的hash值
        // todo 另外还要处理脏配置
        SegmentKey segKey = SEG_SVC.tryAppendSegment(tableKey, StoreType.FINE_IO);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType());
        return new SegmentState(segInfo);
    }
}