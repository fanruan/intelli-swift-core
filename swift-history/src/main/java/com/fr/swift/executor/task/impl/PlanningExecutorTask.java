package com.fr.swift.executor.task.impl;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.bean.PlanningBean;
import com.fr.swift.executor.task.job.impl.PlanningJob;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.source.SourceKey;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description
 * @since swift-1.2.0
 */
public class PlanningExecutorTask extends AbstractExecutorTask<PlanningJob> {

    public PlanningExecutorTask(PlanningBean planningBean) throws Exception {
        super(new SourceKey(SwiftTaskType.PLANNING.name()),
                false,
                SwiftTaskType.PLANNING,
                LockType.TABLE,
                SwiftTaskType.PLANNING.name(),
                DBStatusType.ACTIVE,
                new PlanningJob(planningBean), 10);
    }

    protected PlanningExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType,
                                   LockType lockType, String lockKey, DBStatusType dbStatusType, String taskId,
                                   long createTime, String taskContent, int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);
        PlanningBean planningBean = JsonBuilder.readValue(taskContent, PlanningBean.class);
        this.job = new PlanningJob(planningBean);
    }

    public static ExecutorTask of(String taskContent) throws Exception {
        return new PlanningExecutorTask(JsonBuilder.readValue(taskContent, PlanningBean.class));
    }
}
