package com.fr.swift.cloud.quartz.execute;

import com.fr.swift.cloud.quartz.config.ScheduleTaskType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * @author Heng.J
 * @date 2020/5/13
 * @description
 * @since swift 1.1
 */
public interface ScheduleJob extends Job {

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException;

    String getCronExpression();

    ScheduleTaskType getExecutorType();

    JobKey getJobKey();
}

