package com.fr.swift.segment;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.event.TransferRealtimeListener.TransferRealtimeEventData;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.service.transfer.SegmentTransfer;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/6/5
 */
@SwiftBean(name = "incrementer")
@SwiftScope("prototype")
public class Incrementer<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, SwiftResultSet> {

    public Incrementer(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }

    @Override
    protected Inserting getInserting(SegmentKey segKey) {
        Segment seg = SegmentUtils.newSegment(segKey);
        return new Inserting(SwiftInserter.ofAppendMode(seg), seg, SegmentUtils.safeGetRowCount(seg));
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // 增量块已满，transfer掉
        SegmentKey segKey = newSegmentKey(segInfo);
        SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, TransferRealtimeEventData.ofActive(segKey));
    }

    @Override
    protected void onSucceed() {
        for (SegmentKey importSegKey : importSegKeys) {
            if (!segLocationSvc.containsLocal(importSegKey)) {
                // 不存在则更新seg location
                segLocationSvc.saveOrUpdateLocal(Collections.singleton(importSegKey));
            }
        }
        if (!importSegKeys.isEmpty()) {
            // 发送-1，告诉查询节点，本节点已有该表增量块
            SwiftSegmentEntity allMemSegKeyEntities = new SwiftSegmentEntity(importSegKeys.get(0));
            allMemSegKeyEntities.setSegmentOrder(-1);
            SwiftEventDispatcher.fire(SyncSegmentLocationEvent.PUSH_SEG, Collections.singletonList(allMemSegKeyEntities));
        }
    }

    @Override
    protected void onFailed() {
        // do nothing
    }
}