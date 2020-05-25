package com.fr.swift.quartz.service;

import com.fr.swift.quartz.entity.TaskDefine;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

/**
 * @author Heng.J
 * @date 2020/5/14
 * @description
 * @since swift 1.1
 */
public interface QuartzJobService {

    public void scheduleJob(TaskDefine define) throws SchedulerException;

    public void pauseJob(JobKey jobKey) throws SchedulerException;

    public void resumeJob(JobKey jobKey) throws SchedulerException;

    public boolean deleteJob(JobKey jobKey) throws SchedulerException;

    public boolean modifyJobCron(TaskDefine define);

    public JobDetail getJobDetail(JobKey jobKey, Class<? extends Job> jobClass);
}
