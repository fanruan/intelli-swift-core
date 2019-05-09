package com.fr.swift.executor.task;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.StatusType;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description
 */
public interface ExecutorTask<T extends Job> extends ObjectConverter {

    String getTaskId();

    long getCreateTime();

    ExecutorTaskType getExecutorTaskType();

    SourceKey getSourceKey();

    LockType getLockType();

    String getLockKey();

    /**
     * 数据库执行状态
     *
     * @return
     */
    DBStatusType getDbStatusType();

    void setDbStatusType(DBStatusType dbStatusType);

    /**
     * 内存中执行状态
     *
     * @return
     */
    StatusType getStatusType();

    void setStatusType(StatusType statusType);

    T getJob();

    boolean isPersistent();

    String getTaskContent();
}
