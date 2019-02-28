package com.fr.swift.executor.task.impl;

import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.HistoryImportJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class HistoryImportExecutorTask extends AbstractExecutorTask<Job> {

    public HistoryImportExecutorTask(DataSource dataSource, SwiftSourceAlloter<?, RowInfo> alloter, SwiftResultSet resultSet) {
        super(dataSource.getSourceKey(),
                false,
                ExecutorTaskType.HISTORY,
                LockType.TABLE,
                dataSource.getSourceKey().getId(),
                DBStatusType.ACTIVE,
                new HistoryImportJob(dataSource, alloter, resultSet));
    }
}
