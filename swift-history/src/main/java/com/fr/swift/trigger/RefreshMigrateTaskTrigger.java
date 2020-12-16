package com.fr.swift.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.task.job.schedule.RedistributeMigJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.service.ScheduleTaskService;

/**
 * @author Heng.J
 * @date 2020/11/13
 * @description
 * @since swift-1.2.0
 */
public class RefreshMigrateTaskTrigger implements SwiftPriorityInitTrigger {

    private ScheduleTaskService scheduleTaskService;

    @Override
    public void init() throws Exception {
        SwiftLoggers.getLogger().info("starting schedule refresh migrate task...");
        scheduleTaskService = SwiftContext.get().getBean(ScheduleTaskService.class);
        RedistributeMigJob job = new RedistributeMigJob();
        TaskDefine task = TaskDefine.builder()
                .jobKey(job.getJobKey())
                .cronExpression(job.getCronExpression())
                .jobClass(job.getClass())
                .build();
        scheduleTaskService.addOrUpdateJob(task);
    }

    @Override
    public void destroy() throws Exception {
        SwiftLoggers.getLogger().info("stopping schedule refresh migrate task...");
        scheduleTaskService.deleteJob(RedistributeMigJob.JOBKEY);
    }

    @Override
    public int priority() {
        return Priority.LOWER.priority();
    }
}
