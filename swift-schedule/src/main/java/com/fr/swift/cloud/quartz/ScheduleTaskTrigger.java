package com.fr.swift.cloud.quartz;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.quartz.service.ScheduleTaskService;
import com.fr.swift.cloud.trigger.SwiftPriorityInitTrigger;

/**
 * @author Heng.J
 * @date 2020/5/14
 * @description
 * @since swift 1.1
 */
public class ScheduleTaskTrigger implements SwiftPriorityInitTrigger {

    @Override
    public void init() {
        try {
            ScheduleTaskService scheduleTaskService = SwiftContext.get().getBean(ScheduleTaskService.class);
            SwiftLoggers.getLogger().info("starting schedule task...");
            scheduleTaskService.startup();

        } catch (Exception e) {
            SwiftLoggers.getLogger().error("trigger calc data size task failed : {}", e.getMessage());
        }
    }

    @Override
    public void destroy() {
        ScheduleTaskService scheduleTaskService = SwiftContext.get().getBean(ScheduleTaskService.class);
        SwiftLoggers.getLogger().info("stoping schedule task...");
        scheduleTaskService.shutdown();
    }

    @Override
    public int priority() {
        return Priority.HIGH.priority();
    }
}
