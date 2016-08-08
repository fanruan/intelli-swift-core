package com.fr.bi.cal.generate.timerTask.adapter;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStaff;
import com.finebi.cube.impl.conf.CubeBuildSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.timerTask.TimerTaskSchedule;
import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerScheduleAdapter {
    public static List<TimerTaskSchedule> convertSchedule(long userId, Map<String, UpdateSettingSource> allTimeTaskMap) {
        List<TimerTaskSchedule> scheduleList = new ArrayList<TimerTaskSchedule>();
        for (String keys : allTimeTaskMap.keySet()) {
            UpdateSettingSource settingSource = allTimeTaskMap.get(keys);
            boolean isGlobalUpdate = keys.equals(CubeConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE);
            for (TimeFrequency frequency : settingSource.getTimeList()) {
                String scheduleTime = BIDateUtils.getScheduleTime(frequency.getUpdateTime(), frequency.getUpdateFrequency());
                if (isGlobalUpdate) {
                    CubeBuild cubeBuild = new CubeBuildStaff(new BIUser(userId));
                    TimerTaskSchedule taskSchedule = new TimerTaskSchedule(scheduleTime, cubeBuild, keys,userId);
                    scheduleList.add(taskSchedule);
                } else {
                    BusinessTable table = tableCheck(userId, keys);
                    if (table != null) {
                        CubeBuild cubeBuild = new CubeBuildSingleTable(table, userId);
                        TimerTaskSchedule taskSchedule = new TimerTaskSchedule(scheduleTime, cubeBuild, keys,userId);
                        scheduleList.add(taskSchedule);
                    }
                }
            }
        }
        return scheduleList;
    }

    public static BusinessTable tableCheck(long userId, String keys) {
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            if (table.getTableSource().getSourceID().equals(keys)) {
                return table;
            }
        }

        return null;
    }
}
