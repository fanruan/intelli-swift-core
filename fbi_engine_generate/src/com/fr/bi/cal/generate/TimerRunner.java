package com.fr.bi.cal.generate;


import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.timerTask.BICubeTimeTaskCreatorManager;
import com.fr.bi.cal.generate.timerTask.BICubeTimeTaskCreatorProvider;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.stable.bridge.StableFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * Redesigned by kary on 2015/6/21
 */
public class TimerRunner {

    protected BIUser biUser;
    protected BICubeTimeTaskCreatorProvider biCubeTimeTaskCreatorProvider;
    private List<Timer> timerList = new ArrayList<Timer>();

    public TimerRunner(long userId) {
        biUser = new BIUser(userId);
        biCubeTimeTaskCreatorProvider = StableFactory.getMarkedObject(BICubeTimeTaskCreatorProvider.XML_TAG, BICubeTimeTaskCreatorManager.class);
        reGenerateTimeTasks();
    }

    public void envChanged() {
        reGenerateTimeTasks();
    }

    public void reGenerateTimeTasks() {
        clear();
        resetTimeTasks();
    }

    private void clear() {
        biCubeTimeTaskCreatorProvider.removeTimeTasks(timerList);
    }

    private void resetTimeTasks() {
        Map<String, UpdateSettingSource> allTimeTaskMap = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettings(biUser.getUserId());
        timerList = biCubeTimeTaskCreatorProvider.reGenerateTimeTasks(biUser.getUserId(),allTimeTaskMap);
    }

}
