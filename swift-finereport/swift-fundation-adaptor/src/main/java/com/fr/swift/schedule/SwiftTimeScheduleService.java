package com.fr.swift.schedule;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.exception.FinePackageAbsentException;
import com.finebi.conf.exception.FineTableAbsentException;
import com.finebi.conf.internalimp.update.GlobalUpdateSetting;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.internalimp.update.UpdateTimeItem;
import com.finebi.conf.provider.SwiftPackageConfProvider;
import com.finebi.conf.provider.SwiftTableManager;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.conf.updateInfo.TableUpdateInfoConfigService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.schedule.job.GloableJobTask;
import com.fr.swift.schedule.job.PackageJobTask;
import com.fr.swift.schedule.job.TableJobTask;
import com.fr.swift.constants.UpdateConstants;
import com.fr.third.org.quartz.CronExpression;
import com.fr.third.org.quartz.CronTrigger;
import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobDetail;
import com.fr.third.org.quartz.Scheduler;
import com.fr.third.org.quartz.SchedulerException;
import com.fr.third.org.quartz.SchedulerFactory;
import com.fr.third.org.quartz.SimpleTrigger;
import com.fr.third.org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftTimeScheduleService implements TimeScheduleService {

    private SchedulerFactory sf;

    protected static final TableUpdateInfoConfigService updateInfoConfigService = TableUpdateInfoConfigService.getService();
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftTimeScheduleService.class);

    private static final String JOB_GROUP_NAME = "timeScheduleGroup";
    private static final String TRIGGER_GROUP_NAME = "timeScheduleTrigger";
    public static final int DEFAULT_REPEAT_COUNT = 100000;

    private SwiftTableManager tableManager = StableManager.getContext().getObject("swiftTableManager");
    private SwiftPackageConfProvider packageManager = StableManager.getContext().getObject("swiftPackageConfProvider");

    private static SwiftTimeScheduleService INSTANCE = new SwiftTimeScheduleService();

    private Map<String, List<String>> nameAndTaskNameMap;

    public static SwiftTimeScheduleService getInstance() {
        return INSTANCE;
    }

    private SwiftTimeScheduleService() {
        sf = new StdSchedulerFactory();
        nameAndTaskNameMap = new HashMap<String, List<String>>();
        try {
            resetAllSchedule();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void resetAllSchedule() throws SchedulerException {
        LOGGER.info("=============Remove all schedule begin!!!=============");
        for (Map.Entry<String, List<String>> taskNameEntry : nameAndTaskNameMap.entrySet()) {
            for (String taskName : taskNameEntry.getValue()) {
                LOGGER.info("Remove timer schedule :" + taskName);
                removeQuartzTask(taskName);
            }
        }
        nameAndTaskNameMap.clear();
        LOGGER.info("=============Remove all schedule end!!!=============");
        LOGGER.info("=============Reset all schedule begin!!!=============");
        Map<String, TableUpdateInfo> infoMap = updateInfoConfigService.getAllTableUpdateInfo();
        for (Map.Entry<String, TableUpdateInfo> infoEntry : infoMap.entrySet()) {

            try {
                FineBusinessTable fineBusinessTable = tableManager.getSingleTable(infoEntry.getKey());
                TableUpdateInfo tableUpdateInfo = infoEntry.getValue();
                List<UpdateTimeItem> updateInfoTimeList = tableUpdateInfo.getTimeList();
                if (updateInfoTimeList != null) {
                    List<String> taskNameList = new ArrayList<String>();
                    for (UpdateTimeItem updateTimeItem : updateInfoTimeList) {
                        String taskName = infoEntry.getKey() + "-" + updateTimeItem.getCron();
                        Map<String, String> taskParams = new HashMap<String, String>();
                        taskParams.put("tableName", infoEntry.getKey());
                        taskParams.put("timingType", String.valueOf(updateTimeItem.getSetting().getFrequency().getType()));
                        taskParams.put("updateType", String.valueOf(updateTimeItem.getType()));
//                        ScheduleEntity entity = new ScheduleEntity(taskName, TableJobTask.class, updateTimeItem.getCron(), taskParams,
//                                new Date(updateTimeItem.getStartTime() - 2678400000l), new Date(updateTimeItem.getEndTime()));
                        ScheduleEntity entity = new ScheduleEntity(taskName, TableJobTask.class, updateTimeItem.getCron(), taskParams,
                                new Date(updateTimeItem.getStartTime()), new Date(updateTimeItem.getEndTime()));
                        addScheduleTimeTask(entity);
                        LOGGER.info("Add timer schedule :" + taskName);
                        taskNameList.add(taskName);
                    }
                    nameAndTaskNameMap.put(infoEntry.getKey(), taskNameList);
                }
            } catch (FineTableAbsentException e) {
                try {
                    FineBusinessPackage fineBusinessPackage = packageManager.getSinglePackage(infoEntry.getKey());
                    if (fineBusinessPackage != null) {
                        TableUpdateInfo packageUpdateInfo = infoEntry.getValue();
                        List<UpdateTimeItem> updateInfoTimeList = packageUpdateInfo.getTimeList();
                        if (updateInfoTimeList != null) {
                            List<String> taskNameList = new ArrayList<String>();
                            for (UpdateTimeItem updateTimeItem : updateInfoTimeList) {
                                String taskName = infoEntry.getKey() + "-" + updateTimeItem.getCron();
                                Map<String, String> taskParams = new HashMap<String, String>();
                                taskParams.put("packageId", infoEntry.getKey());
                                taskParams.put("timingType", String.valueOf(updateTimeItem.getSetting().getFrequency().getType()));
//                                ScheduleEntity entity = new ScheduleEntity(taskName, PackageJobTask.class, updateTimeItem.getCron(), taskParams,
//                                        new Date(updateTimeItem.getStartTime() - 2678400000l), new Date(updateTimeItem.getEndTime()));
                                ScheduleEntity entity = new ScheduleEntity(taskName, PackageJobTask.class, updateTimeItem.getCron(), taskParams,
                                        new Date(updateTimeItem.getStartTime()), new Date(updateTimeItem.getEndTime()));
                                addScheduleTimeTask(entity);
                                LOGGER.info("Add timer schedule :" + taskName);
                                taskNameList.add(taskName);
                            }
                            nameAndTaskNameMap.put(infoEntry.getKey(), taskNameList);
                        }
                    }
                } catch (FinePackageAbsentException ignore) {
                }
            }
        }

        GlobalUpdateSetting globalUpdateSetting = updateInfoConfigService.getGlobalUpdateSettings();
        if (globalUpdateSetting != null && globalUpdateSetting.getSettings() != null) {
            List<String> taskNameList = new ArrayList<String>();
            for (UpdateTimeItem updateTimeItem : globalUpdateSetting.getSettings()) {
                String taskName = UpdateConstants.GLOBAL_KEY + "-" + updateTimeItem.getCron();
                Map<String, String> taskParams = new HashMap<String, String>();
                taskParams.put("timingType", String.valueOf(updateTimeItem.getSetting().getFrequency().getType()));
//                ScheduleEntity entity = new ScheduleEntity(taskName, GloableJobTask.class, updateTimeItem.getCron(),
//                        taskParams, new Date(updateTimeItem.getStartTime() - 2678400000l), new Date(updateTimeItem.getEndTime()));
                ScheduleEntity entity = new ScheduleEntity(taskName, GloableJobTask.class, updateTimeItem.getCron(),
                        taskParams, new Date(updateTimeItem.getStartTime()), new Date(updateTimeItem.getEndTime()));
                addScheduleTimeTask(entity);
                LOGGER.info("Add timer schedule :" + taskName);
                taskNameList.add(taskName);
            }
            nameAndTaskNameMap.put(UpdateConstants.GLOBAL_KEY, taskNameList);
        }
        LOGGER.info("=============Reset all schedule end!!!=============");
    }

    private void removeQuartzTask(String jobName) throws SchedulerException {
        Scheduler sched = sf.getScheduler();
        sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);//停止触发器
        sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);//移除触发器
        sched.deleteJob(jobName, JOB_GROUP_NAME);//删除任务
    }

    private <T extends Job> void addScheduleTimeTask(ScheduleEntity entity) {
        String taskName = entity.getTaskName();
        Class<T> jobClass = entity.getJobClass();
        String cronExpression = entity.getCronExpression();
        JobDataMap jobDataMap = new JobDataMap(entity.getTaskParams());
        Date starTime = entity.getStartTime();
        Date endTime = entity.getEndTime();
        try {
            Scheduler sched = sf.getScheduler();
            JobDetail jobDetail = new JobDetail(taskName, JOB_GROUP_NAME, jobClass);//任务名，任务组，任务执行类
            jobDetail.setJobDataMap(jobDataMap);

            int timingType = 0;
            if (jobDataMap.get("timingType") != null) {
                timingType = jobDataMap.getIntegerFromString("timingType");
            }

            if (timingType == 2) {
                sched.scheduleJob(jobDetail, prepareSimpleTrigger(taskName, starTime, endTime, cronExpression));
            } else if (isTaskValid(entity)) {
                sched.scheduleJob(jobDetail, prepareCronTrigger(taskName, starTime, endTime, cronExpression));
            } else {
                LOGGER.warn("the job: " + entity.getTaskName() + "has invalid cronExpression or dateTime: " + entity.getCronExpression() + entity.getStartTime() + entity.getEndTime());
            }

            if (!sched.isShutdown()) {
                sched.start();
            }
            LOGGER.info("Task scheduled with cronExpression :" + cronExpression + " startTime: " + starTime + " and taskName " + taskName + " added successfully");
        } catch (Exception e) {
            LOGGER.warn("Task scheduled with cronExpression :" + cronExpression + " startTime: " + starTime + " and taskName " + taskName + " failed due to ", e);
        }
    }

    private static boolean isTaskValid(ScheduleEntity task) {
        return task.getStartTime().before(task.getEndTime()) &&
                CronExpression.isValidExpression(task.getCronExpression());
    }

    private CronTrigger prepareCronTrigger(String taskName, Date starTime, Date endTime, String cronExpression) throws Exception {
        CronTrigger trigger = new CronTrigger(taskName, TRIGGER_GROUP_NAME);//触发器名,触发器组
        trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        if (starTime == null) {
            starTime = new Date(System.currentTimeMillis());
        }
        trigger.setCronExpression(cronExpression);
        trigger.setStartTime(starTime);
        trigger.setEndTime(endTime);
        return trigger;
    }

    private SimpleTrigger prepareSimpleTrigger(String taskName, Date starTime, Date endTime, String cronExpression) {
        SimpleTrigger trigger = new SimpleTrigger(taskName, TRIGGER_GROUP_NAME);
        trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
        trigger.setStartTime(starTime);
        trigger.setEndTime(endTime);
        trigger.setRepeatCount(DEFAULT_REPEAT_COUNT);
        trigger.setRepeatInterval(Long.valueOf(cronExpression));
        return trigger;
    }
}
