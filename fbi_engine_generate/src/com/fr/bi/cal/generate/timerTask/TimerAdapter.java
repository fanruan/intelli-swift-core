package com.fr.bi.cal.generate.timerTask;

import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.*;

/**
 * Created by Kary on 2016/6/29.
 * 将UpdateSettingSource转为quartz支持的* * * * * *的格式
 */
public class TimerAdapter {
    public static List<TimerTaskSchedule>  convertSchedule(Map<String, UpdateSettingSource> allTimeTaskMap) {
        for (String keys : allTimeTaskMap.keySet()) {
            UpdateSettingSource settingSource = allTimeTaskMap.get(keys);
            boolean isGlobalUpdate = keys.equals(DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE);
            for (TimeFrequency frequency : settingSource.getTimeList()) {
                    Date startDate = BIDateUtils.createStartDate(frequency.getUpdateTime(), frequency.getUpdateFrequency());
                    long period = BIDateUtils.createScheduleTime(frequency.getUpdateTime(), frequency.getUpdateFrequency());
                    TimerTaskSchedule taskSchedule=new TimerTaskSchedule("",isGlobalUpdate,isGlobalUpdate?"":keys);
                }
        }
return null;
    }
}
