package com.fr.swift.quartz.service;

import com.fr.swift.quartz.entity.TaskDefine;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.util.Set;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/3
 */
public interface ScheduleTaskService extends LifeCycle {

    /**
     * get groovy execute job
     * TODO: 2020/9/4  consider it can be split
     *
     * @param fileName
     * @return
     */
    Runnable getGroovyExecuteJob(String fileName) throws Exception;

    /**
     * add or update groovy jobs
     * TODO: 2020/9/3  consider it can be split
     *
     * @param fileNames
     * @throws SchedulerException
     */
    void addOrUpdateGroovyJobs(String... fileNames) throws Exception;

    /**
     * delete groovy jobs
     * TODO: 2020/9/3  consider it can be split
     *
     * @param fileNames
     * @throws SchedulerException
     */
    void deleteGroovyJobs(String... fileNames) throws Exception;

    /**
     * init groovy schedule job
     * TODO: 2020/9/3  consider abstract this method
     *
     * @throws Exception
     */
    void initGroovyScheduleJob() throws Exception;

    /**
     * add or update taskDefine
     *
     * @param define
     * @throws SchedulerException
     */
    void addOrUpdateJob(TaskDefine define) throws SchedulerException;

    /**
     * delete by jobKey
     *
     * @param jobKey
     * @return
     */
    boolean deleteJob(JobKey jobKey) throws SchedulerException;

    /**
     * get current executing quartz jobs
     *
     * @return
     * @throws SchedulerException
     */
    Set<JobKey> getExistJobKeys() throws SchedulerException;
}
