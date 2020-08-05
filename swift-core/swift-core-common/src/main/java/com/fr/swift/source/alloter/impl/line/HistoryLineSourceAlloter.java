package com.fr.swift.source.alloter.impl.line;


import com.fr.swift.config.entity.SwiftSegmentVisitedEntity;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentVisited;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;

/**
 * @author yee
 * @date 2017/12/13
 */
public class HistoryLineSourceAlloter extends BaseLineSourceAlloter {

    public HistoryLineSourceAlloter(SourceKey tableKey, LineAllotRule rule) {
        super(tableKey, rule);
    }

    @Override
    protected SegmentState getInsertableSeg(int logicOrder) {
        // todo 另外还要处理脏配置
        SegmentKey segKey = swiftSegmentService.tryAppendSegment(tableKey, StoreType.FINE_IO);
        SwiftLoggers.getLogger().debug("importing, append new seg {}", segKey);
        SegmentVisited segmentVisited = new SwiftSegmentVisitedEntity(segKey.getId());
        swiftSegmentService.saveVisited(segmentVisited);
        SwiftSegmentInfo segInfo = new SwiftSegmentInfo(segKey.getOrder(), segKey.getStoreType());
        return new SegmentState(segInfo);
    }
}