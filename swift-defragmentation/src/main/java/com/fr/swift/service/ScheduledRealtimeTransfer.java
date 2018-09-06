package com.fr.swift.service;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SegmentTransfer;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.third.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author anchore
 * @date 2018/7/27
 */
@Service
public class ScheduledRealtimeTransfer implements Runnable {
    private static final int MIN_PUT_THRESHOLD = LineAllotRule.MEM_STEP / 2;

    private final SwiftSegmentManager localSegments = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    private final ServiceTaskExecutor taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    private ScheduledRealtimeTransfer() {
        SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(this, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        for (final Table table : SwiftDatabase.getInstance().getAllTables()) {
            for (final SegmentKey segKey : localSegments.getSegmentKeys(table.getSourceKey())) {
                try {
                    if (segKey.getStoreType() != StoreType.MEMORY) {
                        continue;
                    }
                    Segment realtimeSeg = localSegments.getSegment(segKey);
                    if (realtimeSeg.isReadable() && realtimeSeg.getAllShowIndex().getCardinality() > MIN_PUT_THRESHOLD) {

                        taskExecutor.submit(new SwiftServiceCallable(table.getSourceKey(), ServiceTaskType.PERSIST) {
                            @Override
                            public void doJob() {
                                new RealtimeToHistoryTransfer(segKey).transfer();

                            }
                        });
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(String.format("Segkey %s persist failed!", segKey.getTable().getId()), e);
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
    }
}