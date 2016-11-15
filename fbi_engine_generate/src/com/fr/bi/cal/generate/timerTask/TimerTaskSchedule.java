package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.conf.CubeBuildStuff;
import com.fr.bi.stable.constant.DBConstant;

import java.util.UUID;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerTaskSchedule {
    private String timeSchedule;
    private String sourceName;
    private CubeBuildStuff cubeBuild;
    private String jobName;
    private long userId;
    private int updateType;

    public int getUpdateType() {
        return updateType;
    }

    public TimerTaskSchedule(String schedule, CubeBuildStuff cubeBuild, String sourceName, long userId, int updateType) {
        this.timeSchedule = schedule;
        this.sourceName = sourceName == null ? DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE : sourceName;
        this.cubeBuild = cubeBuild;
        this.userId = userId;
        this.jobName = UUID.randomUUID().toString();
        this.updateType = updateType;
    }

    public String getTimeSchedule() {
        return timeSchedule;
    }

    public CubeBuildStuff getCubeBuild() {
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
