package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.conf.CubeBuildStuff;

import java.util.UUID;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerTaskSchedule {
    private String timeSchedule;
    private String sourceName;
    private CubeBuildStuff cubeBuildStuff;
    private String jobName;

    public TimerTaskSchedule(String schedule, CubeBuildStuff cubeBuildStuff, String sourceName) {
        this.timeSchedule = schedule;
        this.sourceName = sourceName;
        this.cubeBuildStuff=cubeBuildStuff;
        this.jobName=sourceName+timeSchedule+ UUID.randomUUID();
    }

    public String getTimeSchedule() {
        return timeSchedule;
    }

    public CubeBuildStuff getCubeBuildStuff() {
        return cubeBuildStuff;
    }

    public String getJobName(){
        return jobName;
    } 
}
