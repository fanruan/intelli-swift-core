package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.TransferRealtimeListener.TransferRealtimeEventData;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.TimeUnit;

/**
 * @author anchore
 * @date 2018/7/27
 */
@SwiftBean
public class ScheduledRealtimeTransfer implements Runnable {
    private static final int MIN_PUT_THRESHOLD = LineAllotRule.MEM_STEP / 2;

    private final SwiftSegmentManager localSegments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private ScheduledRealtimeTransfer() {
        SwiftExecutors.newSingleThreadScheduledExecutor(new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(this, 1, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        for (final Table table : SwiftDatabase.getInstance().getAllTables()) {
            for (final SegmentKey segKey : localSegments.getSegmentKeys(table.getSourceKey())) {
                try {
                    if (segKey.getStoreType().isPersistent()) {
                        continue;
                    }
                    Segment realtimeSeg = localSegments.getSegment(segKey);
                    if (realtimeSeg.isReadable() && realtimeSeg.getAllShowIndex().getCardinality() >= MIN_PUT_THRESHOLD) {
                        SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, TransferRealtimeEventData.ofPassive(segKey));
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("Segkey {} persist failed", segKey.getTable().getId(), e);
                }
            }
        }
    }
}