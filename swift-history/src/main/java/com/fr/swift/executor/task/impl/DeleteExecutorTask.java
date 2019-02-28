package com.fr.swift.executor.task.impl;

import com.fr.swift.db.Where;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.DeleteJob;
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
public class DeleteExecutorTask extends AbstractExecutorTask<Job> {

    public DeleteExecutorTask(SourceKey sourceKey, Where where) {
        super(sourceKey,
                true,
                ExecutorTaskType.DELETE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new DeleteJob(sourceKey, where));
    }
}
