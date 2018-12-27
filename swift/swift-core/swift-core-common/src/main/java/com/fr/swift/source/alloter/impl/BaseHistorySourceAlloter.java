package com.fr.swift.source.alloter.impl;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.RowInfo;

/**
 * @author anchore
 * @date 2018/6/5
 */
public abstract class BaseHistorySourceAlloter<A extends AllotRule, R extends RowInfo> extends BaseSourceAlloter<A, R> {

    public BaseHistorySourceAlloter(SourceKey tableKey, A rule) {
        super(tableKey, rule);
    }

    @Override
    protected SegmentState getInsertableSeg() {
        // todo hash出的seg key可能还要写入此seg key的hash值
        // todo 另外还要处理脏配置
        SegmentKey segKey = SEG_SVC.tryAppendSegment(tableKey, StoreType.FINE_IO);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), StoreType.FINE_IO);
        return new SegmentState(segInfo);
    }
}