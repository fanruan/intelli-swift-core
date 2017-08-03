package com.fr.bi.cal.generate.timerTask;

import com.fr.bi.cal.generate.timerTask.quartz.JobTask;
import com.fr.bi.conf.base.scheduler.BITimeScheduleService;
import com.fr.bi.conf.base.scheduler.BITimeSchedulerManager;
import com.fr.bi.conf.base.scheduler.ScheduleEntity;
import com.fr.stable.bridge.StableFactory;

import java.util.*;

/**
 * Created by neil on 2017/7/31.
 */
public class BICubeTimeSchedulerServiceImpl implements BICubeTimeSchedulerService {

    protected BITimeSchedulerManager timeSchedulerManager;

    public BICubeTimeSchedulerServiceImpl() {
        this.timeSchedulerManager = StableFactory.getMarkedObject(BITimeScheduleService.XML_TAG, BITimeSchedulerManager.class);
    }

    @Override
    public void addTimeScheduler(String cronExpression, String tableKey, long userId, int updateType, Date startTime, Date endTime) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tableKey", tableKey);
        params.put("userId", String.valueOf(userId));
        params.put("updateTye", String.valueOf(updateType));
        timeSchedulerManager.scheduleTimeTask(UUID.randomUUID().toString(), JobTask.class, cronExpression, params, startTime, endTime);
    }

    @Override
    public void removeAllTimeSchedulers() {
        Collection<ScheduleEntity> scheduleEntities = timeSchedulerManager.getAllScheduleEntity();
        if (scheduleEntities != null) {
            Iterator<ScheduleEntity> iterator = scheduleEntities.iterator();
            while (iterator.hasNext()) {
                ScheduleEntity scheduleEntity = iterator.next();
                timeSchedulerManager.removeScheduledTimeTask(scheduleEntity.getTaskName());
            }
        }
    }

    @Override
    public void removeTimeScheduler(String taskName) {
        timeSchedulerManager.removeScheduledTimeTask(taskName);
    }

    @Override
    public void persistData() {
        timeSchedulerManager.writeFile();
    }
}
