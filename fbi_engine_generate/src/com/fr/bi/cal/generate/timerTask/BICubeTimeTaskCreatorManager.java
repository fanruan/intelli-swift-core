package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.*;

/**
 * Created by Kary on 2016/6/28.
 */
public class BICubeTimeTaskCreatorManager implements BICubeTimeTaskCreatorProvider {
    public BICubeTimeTaskCreatorManager() {
    }

    @Override
    public List<Timer> reGenerateTimeTasks(long userId, Map<String, UpdateSettingSource> allTimeTaskMap) {
        List<Timer> timerList = new ArrayList<Timer>();
        for (String keys : allTimeTaskMap.keySet()) {
            if (keys.equals(DBConstant.GLOBAL_UPDATE)) {
                for (TimeFrequency frequency : allTimeTaskMap.get(keys).getTimeList()) {
                    timerList.add(addGlobalTask(frequency, userId));
                }
            } else {
                for (TimeFrequency frequency : allTimeTaskMap.get(keys).getTimeList()) {
                    BusinessTable businessTable = tableCheck(userId, keys);
                    if (businessTable!=null) {
                        timerList.add(addSingleTableTask(frequency, (BIBusinessTable) businessTable, userId));
                    }
                }

            }
        }
        return timerList;
    }

    @Override
    public void removeTimeTasks(List<Timer> timerList) {
        if (timerList != null) {
            for (int i = 0; i < timerList.size(); i++) {
                timerList.get(i).cancel();
            }
            timerList.clear();
        }
    }

    private Timer addGlobalTask(TimeFrequency frequency, final long userId) {
        Date startDate = BIDateUtils.createStartDate(frequency.getUpdateTime(), frequency.getUpdateFrequency());
        Timer timer = new Timer();
        long period = BIDateUtils.createScheduleTime(frequency.getUpdateTime(), frequency.getUpdateFrequency());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManager(new BIUser(userId));
                CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
            }
        }, startDate, period);
        return timer;
    }

    private Timer addSingleTableTask(TimeFrequency frequency, final BIBusinessTable table, final long userId) {
        long period = BIDateUtils.createScheduleTime(frequency.getUpdateTime(), frequency.getUpdateFrequency());
        Date startDate = BIDateUtils.createStartDate(frequency.getUpdateTime(), frequency.getUpdateFrequency());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable(table, userId);
                CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
            }

        }, startDate, period);
        return timer;
    }
    private BusinessTable tableCheck(long userId, String keys){
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            if(table.getTableSource().getSourceID().equals(keys)){
                return table;
            }
        }
        return null;
    }

}
