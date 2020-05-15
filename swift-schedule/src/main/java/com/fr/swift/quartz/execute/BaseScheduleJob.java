package com.fr.swift.quartz.execute;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Heng.J
 * @date 2020/5/13
 * @description
 * @since swift 1.1
 */
public interface BaseScheduleJob extends Job {

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException;

    String getCronExpression();
}

