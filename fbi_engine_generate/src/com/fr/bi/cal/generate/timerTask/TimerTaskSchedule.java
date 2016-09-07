package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.conf.CubeBuild;

import java.util.UUID;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerTaskSchedule {
    private String timeSchedule;
    private String sourceName;
    private CubeBuild cubeBuild;
    private String jobName;
    private long userId;
    private int updateType;

    public int getUpdateType() {
        return updateType;
    }

    public TimerTaskSchedule(String schedule, CubeBuild cubeBuild, String sourceName, long userId, int updateType) {
        this.timeSchedule = schedule;
        this.sourceName = sourceName == null ? "__global_update__" : sourceName;
        this.cubeBuild = cubeBuild;
        this.userId = userId;
        this.jobName = sourceName + timeSchedule + UUID.randomUUID();
        this.updateType = updateType;
    }

    public String getTimeSchedule() {
        return timeSchedule;
    }

    public CubeBuild getCubeBuild() {
        return cubeBuild;
    }

    public long getUserId() {
        return userId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getJobName() {
        return jobName;
    }
}
