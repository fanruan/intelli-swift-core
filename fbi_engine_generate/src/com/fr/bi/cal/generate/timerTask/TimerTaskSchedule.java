package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.conf.CubeBuild;
import com.fr.bi.stable.constant.DBConstant;

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

    public TimerTaskSchedule(String schedule, CubeBuild cubeBuild, String sourceName, long userId) {
        this.timeSchedule = schedule;
        this.sourceName = sourceName==null? DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE:sourceName;
        this.cubeBuild = cubeBuild;
        this.userId=userId;
        this.jobName=sourceName+timeSchedule+ UUID.randomUUID();
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

    public String getJobName(){
        return jobName;
    } 
}
