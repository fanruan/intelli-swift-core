package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.bean.PlanningBean;
import com.fr.swift.executor.task.bean.info.MigrateInfo;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.executor.task.job.schedule.MigrateScheduleJob;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.service.ScheduleTaskService;

import java.util.Collections;
import java.util.List;

/**
 * @author Heng.J
 * @date 2020/10/28
 * @description
 * @since swift-1.2.0
 */
public class PlanningJob extends BaseJob<Boolean, List<String>> {

    private PlanningBean planningBean;

    private ScheduleTaskService scheduleTaskService = SwiftContext.get().getBean(ScheduleTaskService.class);

    public PlanningJob(PlanningBean planningBean) {
        this.planningBean = planningBean;
    }

    @Override
    public Boolean call() throws Exception {
        switch (planningBean.getTaskType()) {
            case MIGRATE: {
                MigrateInfo migrateInfo = ((MigrateInfo) planningBean.getTaskInfo());
                MigrateScheduleJob job = new MigrateScheduleJob();
                TaskDefine task = TaskDefine.builder()
                        .jobKey(job.getJobKey())
                        .cronExpression(migrateInfo.getMigrateTime())
                        .jobClass(job.getClass())
                        .jobData(Collections.singletonMap("migrateBean", migrateInfo.getMigrateBean()))
                        .build();
                scheduleTaskService.addOrUpdateJob(task);
                return true;
            }
        }
        return false;
    }
}