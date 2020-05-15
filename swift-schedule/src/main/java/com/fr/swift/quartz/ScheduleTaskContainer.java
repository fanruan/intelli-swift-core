package com.fr.swift.quartz;

import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.execute.BaseScheduleJob;
import org.quartz.JobKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heng.J
 * @date 2020/5/14
 * @description
 * @since swift 1.1
 */
public class ScheduleTaskContainer {

    public static ScheduleTaskContainer INSTANCE = new ScheduleTaskContainer();

    public static ScheduleTaskContainer getInstance() {
        return INSTANCE;
    }

    private List<TaskDefine> taskDefineList;

    public ScheduleTaskContainer() {
        this.taskDefineList = new ArrayList<>();
    }

    public void schedulerTaskJob(BaseScheduleJob job) {
        TaskDefine task = TaskDefine.builder()
                .jobKey(JobKey.jobKey(job.getClass().getName()))
                .cronExpression(job.getCronExpression())
                .jobClass(job.getClass())
                .build();
        taskDefineList.add(task);
    }

    public List<TaskDefine> getTaskDefineList() {
        return taskDefineList;
    }
}
