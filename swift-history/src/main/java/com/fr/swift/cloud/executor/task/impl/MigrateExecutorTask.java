package com.fr.swift.cloud.executor.task.impl;

import com.fr.swift.cloud.base.json.JsonBuilder;
import com.fr.swift.cloud.executor.task.AbstractExecutorTask;
import com.fr.swift.cloud.executor.task.bean.MigrateBean;
import com.fr.swift.cloud.executor.task.job.impl.MigrateJob;
import com.fr.swift.cloud.executor.type.DBStatusType;
import com.fr.swift.cloud.executor.type.ExecutorTaskType;
import com.fr.swift.cloud.executor.type.LockType;
import com.fr.swift.cloud.executor.type.SwiftTaskType;
import com.fr.swift.cloud.source.SourceKey;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description
 * @since swift-1.2.0
 */
public class MigrateExecutorTask extends AbstractExecutorTask<MigrateJob> {

    public MigrateExecutorTask(MigrateBean migrateBean) throws Exception {
        super(new SourceKey(SwiftTaskType.MIGRATE.name()),
                true,
                SwiftTaskType.MIGRATE,
                LockType.TABLE,
                SwiftTaskType.MIGRATE.name(),
                DBStatusType.ACTIVE,
                new MigrateJob(migrateBean), 0);
    }

    public MigrateExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                               String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                               int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);
        MigrateBean migrateBean = JsonBuilder.readValue(taskContent, MigrateBean.class);
        this.job = new MigrateJob(migrateBean);
    }

    public static MigrateExecutorTask of(MigrateBean migrateBean) throws Exception {
        return new MigrateExecutorTask(migrateBean);
    }
}
