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

    /**
     * 任务优先级，越高在队列中排越前
     */
    int getPriority();

    void setPriority(int priority);

    /**
     * failed 时需要存的报错信息
     *
     * @param cause
     */
    void setCause(String cause);

    /**
     * failed 时需要存的报错信息
     *
     * @return
     */
    String getCause();

    /**
     * 任务结束时需要存的时间点
     *
     * @param finishTime
     */
    void setFinishTime(long finishTime);

    /**
     * 任务结束时需要存的时间点
     */
    long getFinishTime();

    /**
     * 任务开始时需要存的时间点
     */
    void setStartTime(long startTime);

    long getStartTime();
}
