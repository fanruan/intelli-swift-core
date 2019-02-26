package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class HistoryImporterExecutorTask extends AbstractExecutorTask<Job> {

    public HistoryImporterExecutorTask(SourceKey sourceKey, Job job) {
        super(sourceKey, false, ExecutorTaskType.HISTORY, LockType.TABLE, sourceKey.getId(), DBStatusType.ACTIVE, job);
    }
}
