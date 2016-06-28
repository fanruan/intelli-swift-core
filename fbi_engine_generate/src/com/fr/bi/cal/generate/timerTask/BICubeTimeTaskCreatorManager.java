package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.singletable.BICubeTimeTaskCreator;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.conf.manager.update.source.TimeFrequency;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.*;

/**
 * Created by Kary on 2016/6/28.
 */
public class BICubeTimeTaskCreatorManager implements BICubeTimeTaskCreator {
   private List<Timer> timerList = new ArrayList<Timer>();

    @Override
    public void taskCreate(long userId) {
        clear();
        Map<String, UpdateSettingSource> allTimeTaskMap = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettings(userId);
        for (String keys : allTimeTaskMap.keySet()) {
            if (keys.equals(DBConstant.GLOBAL_UPDATE)) {
                for (TimeFrequency frequency : allTimeTaskMap.get(keys).getTimeList()) {
                    GlobalTaskAdd(frequency, userId);
                }
            } else {
                for (TimeFrequency frequency : allTimeTaskMap.get(keys).getTimeList()) {
                    BIBusinessTable table = new BIBusinessTable(new BITableID(keys));
                    SingleTableTaskAdd(frequency, table, userId);
                }

            }
        }
    }

    private void GlobalTaskAdd(TimeFrequency frequency, final long userId) {
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
        timerList.add(timer);
    }

    private void SingleTableTaskAdd(TimeFrequency frequency, final BIBusinessTable table, final long userId) {
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
        timerList.add(timer);
    }

    private void clear(){
        if (timerList != null) {
            for (int i = 0; i < timerList.size(); i++) {
                timerList.get(i).cancel();
            }
            timerList.clear();
        }
    }
    @Override
    public TimerTask createNewObject() {
        return null;
    }
}
