package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.TruncateJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2019/2/27
 */
public class TruncateExecutorTask extends AbstractExecutorTask<Job<Void, SourceKey>> {
    public TruncateExecutorTask(SourceKey sourceKey) throws Exception {
        super(sourceKey,
                true,
                ExecutorTaskType.TRUNCATE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new TruncateJob(sourceKey));
    }

    public TruncateExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent);
        this.job = new TruncateJob(sourceKey);
    }
}