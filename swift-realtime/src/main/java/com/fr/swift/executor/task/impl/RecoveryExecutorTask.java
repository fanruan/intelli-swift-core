package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.RecoveryJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2019/2/26
 */
public class RecoveryExecutorTask extends AbstractExecutorTask<Job<Void, SegmentKey>> {
    public RecoveryExecutorTask(SegmentKey recoverySegKey) throws Exception {
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

    public RecoveryExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent);

        SegmentKey segmentKey = JsonBuilder.readValue(taskContent, SwiftSegmentEntity.class);
        this.job = new RecoveryJob(segmentKey);
    }
}