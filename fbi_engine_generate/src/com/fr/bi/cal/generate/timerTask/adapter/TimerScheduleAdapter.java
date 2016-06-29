package com.fr.bi.cal.generate.timerTask.adapter;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.timerTask.TimerTaskSchedule;
import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kary on 2016/6/29.
 */
public class TimerScheduleAdapter {
    public static List<TimerTaskSchedule>  convertSchedule(long userId, Map<String, UpdateSettingSource> allTimeTaskMap) {
        List<TimerTaskSchedule> scheduleList = new ArrayList<TimerTaskSchedule>();
        for (String keys : allTimeTaskMap.keySet()) {
            UpdateSettingSource settingSource = allTimeTaskMap.get(keys);
            boolean isGlobalUpdate = keys.equals(DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE);
            for (TimeFrequency frequency : settingSource.getTimeList()) {
                String scheduleTime = BIDateUtils.getScheduleTime(frequency.getUpdateTime(), frequency.getUpdateFrequency());
                tableCheck(userId,keys);
                if(isGlobalUpdate){
                    CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManager(new BIUser(userId));
                    TimerTaskSchedule taskSchedule=new TimerTaskSchedule(scheduleTime,cubeBuildStuff,keys);
                    scheduleList.add(taskSchedule);
                }else {
                    BusinessTable table = tableCheck(userId, keys);
                    if(table !=null) {
                        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable((BusinessTable) table, userId);
                        TimerTaskSchedule taskSchedule=new TimerTaskSchedule(scheduleTime,cubeBuildStuff,keys);
                        scheduleList.add(taskSchedule);
                    }
                }
                }
        }
        return scheduleList;
    }
    private static BusinessTable tableCheck(long userId, String keys){
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            if(table.getTableSource().getSourceID().equals(keys)){
                return table;
            }
        }
        return null;
    }
}
