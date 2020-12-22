package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.bean.PlanningBean;
import com.fr.swift.executor.task.info.ClearConflictInfo;
import com.fr.swift.executor.task.info.MigScheduleInfo;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.executor.task.job.schedule.MigrateScheduleJob;
import com.fr.swift.executor.task.job.trigger.MigrateTriggerJob;
import com.fr.swift.executor.task.type.MigTaskType;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.executor.utils.TaskQueueUtils;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.service.ScheduleTaskService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.quartz.JobKey;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Heng.J
 * @date 2020/10/28
 * @description
 * @since swift-1.2.0
 */
public class PlanningJob extends BaseJob<Boolean, PlanningBean> {

    private PlanningBean planningBean;

    private ScheduleTaskService scheduleTaskService = SwiftContext.get().getBean(ScheduleTaskService.class);

    public PlanningJob(PlanningBean planningBean) {
        this.planningBean = planningBean;
    }

    @Override
    public Boolean call() throws Exception {
        ExecutorTaskType type = planningBean.getTaskInfo().type();
        if (Objects.equals(MigTaskType.MIGRATE_SCHEDULE, type)) {
            MigScheduleInfo migScheduleInfo = ((MigScheduleInfo) planningBean.getTaskInfo());
            MigrateScheduleJob job = new MigrateScheduleJob();
            String migrateTime = migScheduleInfo.getMigrateTime();
            TaskDefine task = TaskDefine.builder()
                    .jobKey(JobKey.jobKey(migrateTime, "migrate"))
                    .cronExpression(migrateTime)
                    .jobClass(job.getClass())
                    .jobData(Collections.singletonMap(MigrateBean.KEY, migScheduleInfo.getMigrateBean()))
                    .build();
            scheduleTaskService.addOrUpdateJob(task);
            return true;
        } else if (Objects.equals(MigTaskType.MIGRATE_TRIGGER, type)) {
            MigrateTriggerJob.getInstance().triggerMigrate();
            return true;
        } else if (Objects.equals(MigTaskType.CLEAR_CONFLICT, type)) {
            String migrateIndex = ((ClearConflictInfo) planningBean.getTaskInfo()).getMigrateIndex();
            TaskQueueUtils.clearConflictTasks(((ClearConflictInfo) planningBean.getTaskInfo()).getMigrateIndex());
            ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfResult(result -> Objects.equals(result, false))
                    .retryIfExceptionOfType(Throwable.class)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    .withWaitStrategy(WaitStrategies.incrementingWait(1, TimeUnit.MINUTES, 1, TimeUnit.MINUTES))
                    .build();
            return retryer.call(() -> serviceContext.report(NodeEvent.START_WAITING, NodeMessage.of(SwiftProperty.get().getMachineId(), migrateIndex)));
        }
        return false;
    }

    @Override
    public PlanningBean serializedTag() {
        return planningBean;
    }
}