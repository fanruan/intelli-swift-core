package com.fr.swift.quartz.service;

import com.fr.swift.quartz.entity.TaskDefine;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/5/14
 * @description
 * @since swift 1.1
 */
public interface QuartzJobService extends LifeCycle {

    void scheduleJob(TaskDefine define) throws SchedulerException;

    void pauseJob(JobKey jobKey) throws SchedulerException;

    void resumeJob(JobKey jobKey) throws SchedulerException;

    boolean deleteJob(JobKey jobKey) throws SchedulerException;

    boolean modifyJobCron(TaskDefine define);

    JobDetail getJobDetail(JobKey jobKey, Class<? extends Job> jobClass);

    Set<JobKey> getExistJobKeys() throws SchedulerException;
}
