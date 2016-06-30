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
    private CubeBuildStuff cubeBuildStuff;
    private String jobName;
    private long userId;

    public TimerTaskSchedule(String schedule, CubeBuildStuff cubeBuildStuff, String sourceName,long userId) {
        this.timeSchedule = schedule;
        this.sourceName = sourceName==null? DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE:sourceName;
        this.cubeBuildStuff=cubeBuildStuff;
        this.userId=userId;
        this.jobName=sourceName+timeSchedule+ UUID.randomUUID();
    }

    public String getTimeSchedule() {
        return timeSchedule;
    }

    public CubeBuildStuff getCubeBuildStuff() {
        return cubeBuildStuff;
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
