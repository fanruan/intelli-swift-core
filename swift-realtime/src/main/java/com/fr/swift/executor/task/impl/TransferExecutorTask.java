package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.TransferJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.segment.SegmentKey;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class TransferExecutorTask extends AbstractExecutorTask<Job> {

    public TransferExecutorTask(SegmentKey transferSegKey) {
        super(transferSegKey.getTable(),
                true,
                ExecutorTaskType.TRANSFER,
                LockType.REAL_SEG,
                transferSegKey.getId(),
                DBStatusType.ACTIVE,
                new TransferJob(transferSegKey));
    }
}
