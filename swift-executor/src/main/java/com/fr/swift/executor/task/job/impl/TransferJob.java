package com.fr.swift.executor.task.job.impl;

import com.fr.swift.executor.task.job.Job;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentTransfer;
import com.fr.swift.service.ScheduledRealtimeTransfer;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class TransferJob implements Job<Boolean> {

    private SegmentKey transferSegkey;

    public TransferJob(SegmentKey transferSegkey) {
        this.transferSegkey = transferSegkey;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            SegmentTransfer transfer = new ScheduledRealtimeTransfer.RealtimeToHistoryTransfer(transferSegkey);
            transfer.transfer();
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }
}
