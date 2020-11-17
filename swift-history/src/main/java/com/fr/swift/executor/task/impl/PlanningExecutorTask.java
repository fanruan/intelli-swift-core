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
                true,
                SwiftTaskType.PLANNING,
                LockType.NONE,
                planningBean.getTaskInfo().type().name(),
                DBStatusType.ACTIVE,
                new PlanningJob(planningBean), 10);
    }

    public PlanningExecutorTask(PlanningBean planningBean, String clusterId) throws Exception {
        this(planningBean);
        this.clusterId = clusterId;
    }

    public PlanningExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType,
                                LockType lockType, String lockKey, DBStatusType dbStatusType, String taskId,
                                long createTime, String taskContent, int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);
        PlanningBean planningBean = JsonToBean(taskContent);
        this.job = new PlanningJob(planningBean);
    }

    public static ExecutorTask of(String taskContent) throws Exception {
        return new PlanningExecutorTask(JsonToBean(taskContent));
    }

    public static ExecutorTask ofByCluster(String taskContent, String clusterId) throws Exception {
        return new PlanningExecutorTask(JsonToBean(taskContent), clusterId);
    }

    private static PlanningBean JsonToBean(String taskContent) throws Exception {
        return JsonBuilder.readValue(taskContent, PlanningBean.class);
    }
}
