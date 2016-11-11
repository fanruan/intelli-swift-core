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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimerTaskSchedule that = (TimerTaskSchedule) o;

        if (userId != that.userId) return false;
        if (updateType != that.updateType) return false;
        if (timeSchedule != null ? !timeSchedule.equals(that.timeSchedule) : that.timeSchedule != null) return false;
        if (tableKey != null ? !tableKey.equals(that.tableKey) : that.tableKey != null) return false;
        return jobName != null ? jobName.equals(that.jobName) : that.jobName == null;

    }

    @Override
    public int hashCode() {
        int result = timeSchedule != null ? timeSchedule.hashCode() : 0;
        result = 31 * result + (tableKey != null ? tableKey.hashCode() : 0);
        result = 31 * result + (jobName != null ? jobName.hashCode() : 0);
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + updateType;
        return result;
    }
}
