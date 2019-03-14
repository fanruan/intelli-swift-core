package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.RealtimeInsertJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Strings;


/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 */
public class RealtimeInsertExecutorTask extends AbstractExecutorTask<Job> {

    public RealtimeInsertExecutorTask(SourceKey sourceKey, SwiftResultSet resultSet) throws Exception {
        super(sourceKey,
                false,
                ExecutorTaskType.REALTIME,
                LockType.VIRTUAL_SEG,
                Strings.EMPTY,
                DBStatusType.ACTIVE,
                new RealtimeInsertJob(sourceKey, resultSet));
    }
}
