package com.fr.bi.cal.generate.timerTask;

import com.fr.bi.stable.constant.DBConstant;

import java.util.UUID;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerTaskSchedule {
    private String timeSchedule;
    private String tableKey;
    private String jobName;
    private long userId;
    private int updateType;

    public int getUpdateType() {
        return updateType;
    }

    public TimerTaskSchedule(String schedule, String tableKey, long userId, int updateType) {
        this.timeSchedule = schedule;
        this.tableKey = tableKey == null ? DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE : tableKey;
        this.userId = userId;
        this.jobName = UUID.randomUUID().toString();
        this.updateType = updateType;
    }

    public String getTimeSchedule() {
        return timeSchedule;
    }

    public long getUserId() {
        return userId;
    }

    public String getTableKey() {
        return tableKey;
    }

    public String getJobName() {
        return jobName;
    }

}
