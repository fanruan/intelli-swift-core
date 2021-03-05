package com.fr.swift.cloud.executor.task.impl;

import com.fr.swift.cloud.executor.task.AbstractExecutorTask;
import com.fr.swift.cloud.executor.task.job.Job;
import com.fr.swift.cloud.executor.task.job.impl.RealtimeInsertJob;
import com.fr.swift.cloud.executor.type.DBStatusType;
import com.fr.swift.cloud.executor.type.LockType;
import com.fr.swift.cloud.executor.type.SwiftTaskType;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.util.Strings;


/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 */
public class RealtimeInsertExecutorTask extends AbstractExecutorTask<Job> {

    public RealtimeInsertExecutorTask(SourceKey sourceKey, SwiftResultSet resultSet) throws Exception {
        super(sourceKey,
                false,
                SwiftTaskType.REALTIME,
                LockType.VIRTUAL_SEG,
                Strings.EMPTY,
                DBStatusType.ACTIVE,
                new RealtimeInsertJob(sourceKey, resultSet), 0);
    }
}
