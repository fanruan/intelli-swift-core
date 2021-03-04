package com.fr.swift.cloud.executor.task.impl;

import com.fr.swift.cloud.executor.task.AbstractExecutorTask;
import com.fr.swift.cloud.executor.task.job.Job;
import com.fr.swift.cloud.executor.task.job.impl.TruncateJob;
import com.fr.swift.cloud.executor.type.DBStatusType;
import com.fr.swift.cloud.executor.type.ExecutorTaskType;
import com.fr.swift.cloud.executor.type.LockType;
import com.fr.swift.cloud.executor.type.SwiftTaskType;
import com.fr.swift.cloud.source.SourceKey;

/**
 * @author anchore
 * @date 2019/2/27
 */
public class TruncateExecutorTask extends AbstractExecutorTask<Job<Void, SourceKey>> {
    public TruncateExecutorTask(SourceKey sourceKey) throws Exception {
        super(sourceKey,
                true,
                SwiftTaskType.TRUNCATE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new TruncateJob(sourceKey), 0);
    }

    public TruncateExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                                String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                                int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);
        this.job = new TruncateJob(sourceKey);
    }
}