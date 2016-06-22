package com.fr.bi.cal.generate;


import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.*;

/**
 * Created by GUY on 2015/3/31.
 * edit by kary on 2015/6/21
 */
public class TimerRunner {

    protected List<Timer> timerList = new ArrayList<Timer>();
    protected BIUser biUser;

    public TimerRunner(long userId) {
        biUser = new BIUser(userId);
        init();
    }

    public void init() {
        if (timerList != null) {
            for (int i = 0; i < timerList.size(); i++) {
                timerList.get(i).cancel();
            }
            timerList.clear();
        }
        timerList = new ArrayList<Timer>();
        Map<String, UpdateSettingSource> allTimeTaskMap = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettings(biUser.getUserId());
        for (String keys : allTimeTaskMap.keySet()) {
            if (keys.equals(DBConstant.GLOBAL_UPDATE)) {
                for (TimeFrequency frequency : allTimeTaskMap.get(keys).getTimeList()) {
                    GlobalTaskAdd(frequency);
                }
            } else {
                for (TimeFrequency frequency : allTimeTaskMap.get(keys).getTimeList()) {
                    BIBusinessTable table = new BIBusinessTable(new BITableID(keys));
                    SingleTableTaskAdd(frequency, table);
                }

            }
        }
    }

    private void GlobalTaskAdd(TimeFrequency frequency) {
        Date startDate = BIDateUtils.createStartDate(frequency.getUpdateTime(), frequency.getUpdateFrequency());
        Timer timer = new Timer();
        long period=BIDateUtils.createScheduleTime(frequency.getUpdateTime(),frequency.getUpdateFrequency());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManager(biUser);
                CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(biUser, cubeBuildStuff), biUser.getUserId());
            }

        }, startDate, period);
        timerList.add(timer);
    }

    private void SingleTableTaskAdd(TimeFrequency frequency, final BIBusinessTable table) {
        long period=BIDateUtils.createScheduleTime(frequency.getUpdateTime(),frequency.getUpdateFrequency());
        Date startDate = BIDateUtils.createStartDate(frequency.getUpdateTime(), frequency.getUpdateFrequency());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable(table, biUser.getUserId());
                CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(biUser, cubeBuildStuff), biUser.getUserId());
            }

        }, startDate, period);
        timerList.add(timer);
    }

    public void envChanged() {
        if (timerList != null) {
            for (int i = 0; i < timerList.size(); i++) {
                timerList.get(i).cancel();
            }
            timerList.clear();
        }
    }
}
