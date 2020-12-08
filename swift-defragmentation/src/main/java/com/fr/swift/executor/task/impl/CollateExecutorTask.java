package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.bean.CollateBean;
import com.fr.swift.executor.task.job.Job;
import com.fr.swift.executor.task.job.impl.CollateJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class CollateExecutorTask extends AbstractExecutorTask<Job> {

    public CollateExecutorTask(CollateBean collateBean) throws Exception {
        super(collateBean.getSourceKey(),
                true,
                SwiftTaskType.COLLATE,
                LockType.TABLE,
                collateBean.getSourceKey().getId(),
                DBStatusType.ACTIVE,
                new CollateJob(collateBean), 0);
    }

    public CollateExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                               String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                               int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);
        CollateBean collateBean = JsonBuilder.readValue(taskContent, CollateBean.class);
        this.job = new CollateJob(collateBean);
    }
}
