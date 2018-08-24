package com.fr.swift.service;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SegmentTransfer;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.concurrent.TimeUnit;

/**
 * @author anchore
 * @date 2018/7/27
 */
public class ScheduledRealtimeTransfer implements Runnable {
    private static final int MIN_PUT_THRESHOLD = LineAllotRule.MEM_STEP / 2;

    private final SwiftSegmentManager localSegments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    public ScheduledRealtimeTransfer() {
        SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(this, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        for (final Table table : SwiftDatabase.getInstance().getAllTables()) {
            for (SegmentKey segKey : localSegments.getSegmentKeys(table.getSourceKey())) {
                if (segKey.getStoreType() != StoreType.MEMORY) {
                    continue;
                }
                Segment realtimeSeg = localSegments.getSegment(segKey);
                if (realtimeSeg.isReadable() && realtimeSeg.getAllShowIndex().getCardinality() > MIN_PUT_THRESHOLD) {
                    new RealtimeToHistoryTransfer(segKey).transfer();
                }
            }
        }
    }

    public static class RealtimeToHistoryTransfer extends SegmentTransfer {
        public RealtimeToHistoryTransfer(SegmentKey realtimeSegKey) {
            super(realtimeSegKey, getHistorySegKey(realtimeSegKey));
        }

        private static SegmentKey getHistorySegKey(SegmentKey realtimeSegKey) {
            return new SegmentKeyBean(realtimeSegKey.getTable().getId(), realtimeSegKey.getUri(), realtimeSegKey.getOrder(), StoreType.FINE_IO, realtimeSegKey.getSwiftSchema());
        }

        @Override
        protected void onSucceed() {
            super.onSucceed();
            triggerCollate(oldSegKey.getTable());
        }
    }

    private static void triggerCollate(SourceKey tableKey) {
        try {
            SwiftContext.get().getBean(CollateService.class).autoCollateHistory(tableKey);
        } catch (Exception ignore) {
        }
    }
}