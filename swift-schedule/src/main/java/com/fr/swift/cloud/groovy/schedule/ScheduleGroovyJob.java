package com.fr.swift.cloud.groovy.schedule;

import com.fr.swift.cloud.quartz.config.ScheduleTaskType;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/4
 */
public interface ScheduleGroovyJob {
    void execute(JobExecutionContext context) throws JobExecutionException;

    String getCronExpression();

    ScheduleTaskType getExecutorType();

    String jobKey();
}
