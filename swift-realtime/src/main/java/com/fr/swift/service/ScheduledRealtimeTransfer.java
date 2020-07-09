package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.TransferRealtimeListener.TransferRealtimeEventData;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;

/**
 * @author anchore
 * @date 2018/7/27
 */
@SwiftBean
public class ScheduledRealtimeTransfer implements Runnable {
    private static final int MIN_PUT_THRESHOLD = LineAllotRule.MEM_STEP / 2;

    private final SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);

    private ScheduledRealtimeTransfer() {
        // TODO: 2020/6/17 先注释
//        SwiftExecutors.newSingleThreadScheduledExecutor(new PoolThreadFactory(getClass())).
//                scheduleWithFixedDelay(this, 1, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        for (final Table table : SwiftDatabase.getInstance().getAllTables()) {
            for (final SegmentKey segKey : segmentService.getSegmentKeys(table.getSourceKey())) {
                try {
                    if (segKey.getStoreType().isPersistent()) {
                        continue;
                    }
                    Segment realtimeSeg = segmentService.getSegment(segKey);
                    if (realtimeSeg.isReadable() && realtimeSeg.getAllShowIndex().getCardinality() >= MIN_PUT_THRESHOLD) {
                        SwiftEventDispatcher.asyncFire(SegmentEvent.TRANSFER_REALTIME, TransferRealtimeEventData.ofPassive(segKey));
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("Segkey {} persist failed", segKey.getTable().getId(), e);
                }
            }
        }
    }
}