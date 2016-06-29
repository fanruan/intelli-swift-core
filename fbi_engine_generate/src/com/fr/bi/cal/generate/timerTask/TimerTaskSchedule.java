package com.fr.bi.cal.generate.timerTask;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerTaskSchedule {
    String timeSchedule;
    String SourceId;
    boolean cubeUpdateType;

    public TimerTaskSchedule(String schedule, boolean cubeUpdateType,String sourceId) {
        this.timeSchedule = schedule;
        SourceId = sourceId;
        this.cubeUpdateType = cubeUpdateType;
    }

    public String getTimeSchedule() {
        return timeSchedule;
    }

    public String getSourceId() {
        return SourceId;
    }

    public boolean isCubeUpdateType() {
        return cubeUpdateType;
    }
}
