package com.fr.swift.service.transfer;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.segment.RealtimeSegment;
import com.fr.swift.segment.RealtimeSegmentMemMeter;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.third.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anchore
 * @date 2018/7/27
 */
@SwiftBean
public class ScheduledRealtimeTransfer implements Runnable {
    private final SwiftSegmentService segSvc = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    private Map<SegmentKey, AtomicInteger> segAges = new HashMap<SegmentKey, AtomicInteger>();

    private ScheduledRealtimeTransfer() {
        SwiftExecutors.newSingleThreadScheduledExecutor(new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(this, 1, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        Map<SegmentKey, Long> segMems = new HashMap<SegmentKey, Long>();

        long usedSegMem = 0;

        for (Entry<SourceKey, List<SegmentKey>> entry : segSvc.getOwnSegments().entrySet()) {
            for (SegmentKey segKey : entry.getValue()) {
                if (segKey.getStoreType().isPersistent()) {
                    continue;
                }

                Segment realtimeSeg = SegmentUtils.newSegment(segKey);
                // 大于5w条的直接transfer
                if (realtimeSeg.isReadable() &&
                        realtimeSeg.getRowCount() >= TransferLimits.MIN_PUT_THRESHOLD) {
                    SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, segKey);
                } else {
                    long segMem = RealtimeSegmentMemMeter.meter((RealtimeSegment) realtimeSeg);
                    segMems.put(segKey, segMem);
                    usedSegMem += segMem;
                }
            }
        }

        List<Entry<SegmentKey, Long>> sortedSegMems = new ArrayList<Entry<SegmentKey, Long>>(segMems.entrySet());
        Collections.sort(sortedSegMems, new SegMemDescCmp());

        // 如超过内存限制，持久化最大的seg，直到低于限制60%
        double safeSegMemLimit = TransferLimits.ALL_SEG_MEM_LIMIT * 0.6;
        for (Entry<SegmentKey, Long> entry : sortedSegMems) {
            if (usedSegMem < safeSegMemLimit) {
                break;
            }
            SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, entry.getKey());
            usedSegMem -= entry.getValue();

            segMems.remove(entry.getKey());
        }

        for (SegmentKey segKey : segMems.keySet()) {
            if (!segAges.containsKey(segKey)) {
                segAges.put(segKey, new AtomicInteger(0));
            }
            segAges.get(segKey).incrementAndGet();
        }

        for (Iterator<Entry<SegmentKey, AtomicInteger>> itr = segAges.entrySet().iterator(); itr.hasNext(); ) {
            Entry<SegmentKey, AtomicInteger> entry = itr.next();
            if (entry.getValue().get() >= TransferLimits.MAX_AGE &&
                    segMems.get(entry.getKey()) >= TransferLimits.MIN_OLD_SEG_MEM) {
                SwiftEventDispatcher.fire(SegmentEvent.TRANSFER_REALTIME, entry.getKey());
                itr.remove();
            }
        }
    }

    private static class SegMemDescCmp implements Comparator<Entry<SegmentKey, Long>> {
        @Override
        public int compare(Entry<SegmentKey, Long> o1, Entry<SegmentKey, Long> o2) {
            return (int) (o2.getValue() - o1.getValue());
        }
    }
}