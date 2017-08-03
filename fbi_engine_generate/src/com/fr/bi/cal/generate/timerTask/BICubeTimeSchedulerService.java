package com.fr.bi.cal.generate.timerTask;


import java.util.Date;

/**
 * Created by neil on 2017/7/31.
 */
public interface BICubeTimeSchedulerService {

    void addTimeScheduler(String cronExpression, String tableKey, long user, int updateType, Date startTime, Date endTime);

    void removeAllTimeSchedulers();

    void removeTimeScheduler(String taskName);

}
