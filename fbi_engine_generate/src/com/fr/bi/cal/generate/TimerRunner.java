package com.fr.bi.cal.generate;


import com.finebi.cube.conf.*;
import com.finebi.cube.conf.singletable.BICubeTimeTaskCreator;
import com.finebi.cube.conf.singletable.TableUpdate;
import com.finebi.cube.conf.timer.UpdateFrequency;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.utils.time.BIDateUtils;

import java.util.*;

/**
 * Created by GUY on 2015/3/31.
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
        Iterator<UpdateFrequency> iter = BICubeConfigureCenter.getTableUpdateFreguency().getUpdateListIterator();
        while (iter.hasNext()) {
            Timer timer = new Timer();
            UpdateFrequency uf = iter.next();
            int hour = uf.getUpdateHour();
            int frequency = uf.getFrequency();
            Date startDate = BIDateUtils.createStartDate(hour, frequency);
            long scheduleTime = 1;
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManager(biUser);
                    CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(biUser,cubeBuildStuff),biUser.getUserId());
                }

            }, startDate, scheduleTime);
            timerList.add(timer);
        }
        Iterator<TableUpdate> iter1 = BICubeConfigureCenter.getTableUpdateManager().getSingleTableUpdateActionIter();
        while (iter1.hasNext()) {
            final TableUpdate action = iter1.next();
            action.scheduleStart(new BICubeTimeTaskCreator() {
                @Override
                public TimerTask createNewObject() {
                    return new TimerTask() {
                        @Override
                        public void run() {
                            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable(action.getTableKey(),biUser.getUserId());
                            CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(biUser,cubeBuildStuff),biUser.getUserId());
                        }
                    };
                }
            });
        }
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
