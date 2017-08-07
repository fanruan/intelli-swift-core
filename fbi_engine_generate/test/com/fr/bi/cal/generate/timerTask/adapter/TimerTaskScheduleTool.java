package com.fr.bi.cal.generate.timerTask.adapter;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kary on 2016/9/5.
 */
public class TimerTaskScheduleTool {
    private final static String jobName = "jobName";

    public static TimerTaskSchedule getFirstDayOfMonthTask() {
        String firstDayOfMonth = BIDateUtils.getScheduleTime(DBConstant.UPDATE_FREQUENCY.EVER_MONTH, 1);
        return new TimerTaskSchedule(firstDayOfMonth, null, jobName, -999, 0);
    }

    public static TimerTaskSchedule getFirstHourOfDayTask() {
        String firstHourOfDay = BIDateUtils.getScheduleTime(DBConstant.UPDATE_FREQUENCY.EVER_DAY, 1);
        return new TimerTaskSchedule(firstHourOfDay, null, jobName, -999, 0);
    }

    public static TimerTaskSchedule getEveryMondayTask() {
        String firstDayOfMonth = BIDateUtils.getScheduleTime(Calendar.MONDAY, 1);
        return new TimerTaskSchedule(firstDayOfMonth, null, jobName, -999, 0);
    }

    public static TimerTaskSchedule getSecondsTask() {
        TimerTaskSchedule schedule=new TimerTaskSchedule("0/2 * * * * ?",null,jobName,-999,0);
        return schedule;
    }

    public static String getJobName() {
        return jobName;
    }

    public static List<TimerTaskSchedule> getAllTasks() {
        List<TimerTaskSchedule> scheduleList = new ArrayList<TimerTaskSchedule>();
        scheduleList.add(getFirstDayOfMonthTask());
        scheduleList.add(getFirstHourOfDayTask());
        scheduleList.add(getEveryMondayTask());
        scheduleList.add(getSecondsTask());
        return scheduleList;
    }
}
