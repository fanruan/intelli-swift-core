package com.fr.bi.cal.generate.timerTask;


import com.fr.bi.conf.base.scheduler.ScheduleEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by neil on 2017/7/31.
 */
public interface BICubeTimeSchedulerService {

    void addTimeScheduler(String cronExpression, String tableKey, long user, int updateType, Date startTime, Date endTime);

    void addTimeSchedulerList(List<ScheduleEntity> entities);

    void removeAllTimeSchedulers();

    void removeTimeScheduler(String taskName);

}
