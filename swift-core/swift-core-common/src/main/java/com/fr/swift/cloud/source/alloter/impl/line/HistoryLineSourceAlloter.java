package com.fr.swift.cloud.source.alloter.impl.line;


import com.fr.swift.cloud.cube.io.Types.StoreType;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentSource;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.alloter.impl.BaseSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.SwiftSegmentInfo;

/**
 * @author yee
 * @date 2017/12/13
 */
public class HistoryLineSourceAlloter extends BaseLineSourceAlloter {

    public HistoryLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected BaseSourceAlloter.SegmentState getInsertableSeg(int logicOrder) {
        // todo 另外还要处理脏配置
        SegmentKey segKey = swiftSegmentService.tryAppendSegment(tableKey, StoreType.FINE_IO, SegmentSource.CREATED, rule.getCubePath(logicOrder));
        SwiftLoggers.getLogger().debug("importing, append new seg {}", segKey);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType(), segKey.getSegmentUri());
        return new BaseSourceAlloter.SegmentState(segInfo);
    }
}