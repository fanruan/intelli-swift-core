package com.fr.bi.cal.generate.timerTask.adapter;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffSingleTable;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.timerTask.TimerTaskSchedule;
import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerScheduleAdapter {
    public static List<TimerTaskSchedule> convertSchedule(long userId, Map<String, UpdateSettingSource> allTimeTaskMap) {
        List<TimerTaskSchedule> scheduleList = new ArrayList<TimerTaskSchedule>();
        Set<String> timeListSet = new HashSet<String>();
        for (String keys : allTimeTaskMap.keySet()) {
            timeListSet.clear();
            UpdateSettingSource settingSource = allTimeTaskMap.get(keys);
            boolean isGlobalUpdate = keys.equals(DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE);
            for (TimeFrequency frequency : settingSource.getTimeList()) {
                String scheduleTime = BIDateUtils.getScheduleTime(frequency.getUpdateTime(), frequency.getUpdateFrequency());
                if (timeListSet.contains(scheduleTime)) {
                    continue;
                }
                timeListSet.add(scheduleTime);
                if (isGlobalUpdate) {
                    CubeBuildStuff cubeBuild = new CubeBuildStuffComplete(new BIUser(userId));
                    TimerTaskSchedule taskSchedule = new TimerTaskSchedule(scheduleTime, cubeBuild, keys, userId, DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
                    scheduleList.add(taskSchedule);
                } else {
                    BusinessTable table = tableCheck(userId, keys);
                    if (table != null) {
                        TimerTaskSchedule taskSchedule = setBaseTableUpdateSettings(userId, frequency, scheduleTime, table);
                        scheduleList.add(taskSchedule);
                        List<TimerTaskSchedule> EtlList = seEtlUpdateSettings(userId, keys, frequency, scheduleTime, table);
                        scheduleList.addAll(EtlList);
                    }
                }
            }
        }
        return scheduleList;
    }

    private static TimerTaskSchedule setBaseTableUpdateSettings(long userId, TimeFrequency frequency, String scheduleTime, BusinessTable table) {
        CubeBuildStuff cubeBuild = new CubeBuildStuffSingleTable(table, table.getTableSource().getSourceID(), userId, frequency.getUpdateType());
        return new TimerTaskSchedule(scheduleTime, cubeBuild, table.getTableSource().getTableName(), userId, frequency.getUpdateType());
    }

    private static List<TimerTaskSchedule> seEtlUpdateSettings(long userId, String keys, TimeFrequency frequency, String scheduleTime, BusinessTable table) {
        List<TimerTaskSchedule> scheduleList = new ArrayList<TimerTaskSchedule>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getDataSourceManager().getAllBusinessTable()) {
            Map<BICore, CubeTableSource> sourceMap = businessTable.getTableSource().createSourceMap();
            for (BICore biCore : sourceMap.keySet()) {
                if (ComparatorUtils.equals(sourceMap.get(biCore).getSourceID(), keys) && sourceMap.size() > 1) {
                    CubeBuildStuff EtlCubeBuild = new CubeBuildStuffSingleTable(businessTable, table.getTableSource().getSourceID(), userId, frequency.getUpdateType());
                    scheduleList.add(new TimerTaskSchedule(scheduleTime, EtlCubeBuild, businessTable.getTableSource().getTableName(), userId, frequency.getUpdateType()));
                }
            }
        }
        return scheduleList;
    }

    public static BusinessTable tableCheck(long userId, String keys) {
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            for (BICore biCore : table.getTableSource().createSourceMap().keySet()) {
                if (table.getTableSource().createSourceMap().get(biCore).getSourceID().equals(keys)) {
                    return table;
                }
            }
        }
        return null;
    }

}
