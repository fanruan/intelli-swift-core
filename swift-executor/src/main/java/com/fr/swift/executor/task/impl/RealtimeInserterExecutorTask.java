package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class RealtimeInserterExecutorTask extends AbstractExecutorTask<Job> {

    public RealtimeInserterExecutorTask(SourceKey sourceKey, Job job) {
        super(sourceKey, true, ExecutorTaskType.REALTIME, LockType.VIRTUAL_SEG, LockType.VIRTUAL_SEG.name(), DBStatusType.ACTIVE, job);
    }
}
