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
public class TruncateExecutorTask extends AbstractExecutorTask<Job<Void>> {
    public TruncateExecutorTask(SourceKey sourceKey) {
        super(sourceKey,
                true,
                ExecutorTaskType.TRUNCATE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new TruncateJob(sourceKey));
    }
}