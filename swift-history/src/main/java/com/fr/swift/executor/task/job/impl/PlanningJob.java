package com.fr.swift.executor.task.job.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.bean.PlanningBean;
import com.fr.swift.executor.task.info.MigrateInfo;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.executor.task.job.schedule.MigrateScheduleJob;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.service.ScheduleTaskService;
import org.quartz.JobKey;

import java.util.Collections;

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
        switch (planningBean.getTaskInfo().type()) {
            case MIGRATE: {
                MigrateInfo migrateInfo = ((MigrateInfo) planningBean.getTaskInfo());
                MigrateScheduleJob job = new MigrateScheduleJob();
                String migrateTime = migrateInfo.getMigrateTime();
                TaskDefine task = TaskDefine.builder()
                        .jobKey(JobKey.jobKey(migrateTime, "migrate"))
                        .cronExpression(migrateTime)
                        .jobClass(job.getClass())
                        .jobData(Collections.singletonMap(MigrateBean.KEY, migrateInfo.getMigrateBean()))
                        .build();
                scheduleTaskService.addOrUpdateJob(task);
                return true;
            }
        }
        return false;
    }

    @Override
    public PlanningBean serializedTag() {
        return planningBean;
    }
}