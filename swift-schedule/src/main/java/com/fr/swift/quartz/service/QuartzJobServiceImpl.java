package com.fr.swift.quartz.service;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.quartz.config.SchedulerProperty;
import com.fr.swift.quartz.entity.TaskDefine;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/5/13
 * @description 定时任务的具体执行逻辑
 * @since swift 1.1
 */
public class QuartzJobServiceImpl extends AbstractLifeCycle implements QuartzJobService {

    private Scheduler scheduler;


    @Override
    public void startup() throws LifeCycleException {
        super.startup();
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory(SchedulerProperty.get().getProperties());
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new LifeCycleException(e);
        }
    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new LifeCycleException(e);
        }
    }

    @Override
    public synchronized void scheduleJob(TaskDefine define) throws SchedulerException {
        JobKey jobKey = define.getJobKey();
        if (scheduler.getJobDetail(jobKey) != null) {
            scheduler.deleteJob(jobKey);
        }
        Class<? extends Job> jobClass = define.getJobClass();
        String cron = define.getCronExpression();
        JobDetail jobDetail = getJobDetail(jobKey, jobClass);
        if (define.getJobDataMap() != null) {
            define.getJobDataMap().forEach((k, v) -> jobDetail.getJobDataMap().put(String.valueOf(k), v));
        }
        Trigger trigger = getTrigger(jobKey, cron);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    public void pauseJob(JobKey jobKey) throws SchedulerException {
        scheduler.pauseJob(jobKey);
    }

    @Override
    public void resumeJob(JobKey jobKey) throws SchedulerException {
        scheduler.resumeJob(jobKey);
    }

    @Override
    public boolean deleteJob(JobKey jobKey) throws SchedulerException {
        return scheduler.deleteJob(jobKey);
    }

    @Override
    public boolean modifyJobCron(TaskDefine define) {
        String cronExpression = define.getCronExpression();
        if (!CronExpression.isValidExpression(cronExpression)) {
            return false;
        }
        JobKey jobKey = define.getJobKey();
        TriggerKey triggerKey = new TriggerKey(jobKey.getName(), jobKey.getGroup());
        try {
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (!cronTrigger.getCronExpression().equalsIgnoreCase(cronExpression)) {
                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (SchedulerException e) {
            SwiftLoggers.getLogger().error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public JobDetail getJobDetail(JobKey jobKey, Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .requestRecovery()
                .storeDurably()
                .build();
    }

    @Override
    public Set<JobKey> getExistJobKeys() throws SchedulerException {
        return scheduler.getJobKeys(GroupMatcher.anyGroup());
    }

    public Trigger getTrigger(JobKey jobKey, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }
}
