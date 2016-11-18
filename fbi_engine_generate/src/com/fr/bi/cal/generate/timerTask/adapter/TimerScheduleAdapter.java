package com.fr.bi.cal.generate.timerTask.adapter;

import com.fr.bi.cal.generate.timerTask.TimerTaskSchedule;
import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerScheduleAdapter {
    public static List<TimerTaskSchedule> convertSchedule(long userId, Map<String, UpdateSettingSource> allTimeTaskMap) {
        List<TimerTaskSchedule> scheduleList = new ArrayList<TimerTaskSchedule>();
        for (String keys : allTimeTaskMap.keySet()) {
            UpdateSettingSource settingSource = allTimeTaskMap.get(keys);
            for (TimeFrequency frequency : settingSource.getTimeList()) {
                String scheduleTime = BIDateUtils.getScheduleTime(frequency.getUpdateTime(), frequency.getUpdateFrequency());
                TimerTaskSchedule taskSchedule = new TimerTaskSchedule(scheduleTime, keys, userId, frequency.getUpdateType());
                scheduleList.add(taskSchedule);
            }
        }
        scheduleList = filterSchedule(scheduleList);
        return scheduleList;
    }

    private static List<TimerTaskSchedule> filterSchedule(List<TimerTaskSchedule> scheduleList) {
        return scheduleList;
    }
}
