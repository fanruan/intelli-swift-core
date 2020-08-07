package com.fr.swift.quartz;

import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.service.QuartzJobService;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;
import org.quartz.SchedulerException;

import java.util.List;

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
            QuartzJobService quartzJobService = SwiftContext.get().getBean(QuartzJobService.class);
            SwiftLoggers.getLogger().info("starting schedule task...");
            List<TaskDefine> taskDefineList = ScheduleTaskContainer.getInstance().getTaskDefineList();
            for (TaskDefine task : taskDefineList) {
                quartzJobService.scheduleJob(task);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("trigger calc data size task failed : {}", e.getMessage());
        }
    }

    @Override
    public void destroy() throws SchedulerException {
        QuartzJobService quartzJobService = SwiftContext.get().getBean(QuartzJobService.class);
        SwiftLoggers.getLogger().info("stopping schedule task...");
        quartzJobService.stop();
    }

    @Override
    public int priority() {
        return Priority.LOW.priority();
    }
}
