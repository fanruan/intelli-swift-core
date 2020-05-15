package com.fr.swift.quartz;

import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.service.QuartzJobService;

import java.util.List;

/**
 * @author Heng.J
 * @date 2020/5/14
 * @description
 * @since swift 1.1
 */
public class ScheduleTaskTrigger {

    // 正常应该用数据库控制分布式下任务执行
    public static final String SCHEDULE_MACHINE_ID = "CLOUD_1";

    private QuartzJobService quartzJobService = SwiftContext.get().getBean(QuartzJobService.class);

    public static ScheduleTaskTrigger INSTANCE = new ScheduleTaskTrigger();

    public static ScheduleTaskTrigger getInstance() {
        return INSTANCE;
    }

    public void trigger() {
        try {
            List<TaskDefine> taskDefineList = ScheduleTaskContainer.getInstance().getTaskDefineList();
            if (SCHEDULE_MACHINE_ID.equals(SwiftProperty.get().getMachineId())) {
                for (TaskDefine task : taskDefineList) {
                    quartzJobService.scheduleJob(task);
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("trigger calc data size task failed : {}", e.getMessage());
        }
    }
}
