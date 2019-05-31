package com.fr.swift.executor.task.job.impl;

import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.SegmentTransfer;
import com.fr.swift.service.ScheduledRealtimeTransfer;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class TransferJob extends BaseJob<Boolean, SegmentKey> {

    private SegmentKey transferSegKey;

    public TransferJob(SegmentKey transferSegKey) {
        this.transferSegKey = transferSegKey;
    }

    @Override
    public Boolean call() {
        try {
            SegmentTransfer transfer = new ScheduledRealtimeTransfer.RealtimeToHistoryTransfer(transferSegKey);
            transfer.transfer();
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public SegmentKey serializedTag() {
        return transferSegKey;
    }
}
