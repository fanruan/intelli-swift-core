package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.RecoveryJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.segment.SegmentKey;

/**
 * @author anchore
 * @date 2019/2/26
 */
public class RecoveryExecutorTask extends AbstractExecutorTask<Job<Void>> {
    public RecoveryExecutorTask(SegmentKey recoverySegKey) {
        super(recoverySegKey.getTable(),
                false,
                ExecutorTaskType.RECOVERY,
                LockType.REAL_SEG,
                recoverySegKey.getId(),
                DBStatusType.ACTIVE,
                new RecoveryJob(recoverySegKey));
        // 优先做恢复任务
        this.createTime = 0;
    }
}