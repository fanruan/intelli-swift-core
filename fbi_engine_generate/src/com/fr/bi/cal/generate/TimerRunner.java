package com.fr.bi.cal.generate;


import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.timerTask.BICubeTimeTaskCreatorManager;
import com.fr.bi.cal.generate.timerTask.BICubeTimeTaskCreatorProvider;
import com.fr.bi.cal.generate.timerTask.TimerTaskSchedule;
import com.fr.bi.cal.generate.timerTask.adapter.TimerScheduleAdapter;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.stable.bridge.StableFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Redesigned by kary on 2015/6/21
 */
public class TimerRunner {

    protected BIUser biUser;
    protected BICubeTimeTaskCreatorProvider biCubeTimeTaskCreatorProvider;
    private List<TimerTaskSchedule> scheduleList;

    public TimerRunner(long userId) {
        biUser = new BIUser(userId);
        biCubeTimeTaskCreatorProvider = StableFactory.getMarkedObject(BICubeTimeTaskCreatorProvider.XML_TAG, BICubeTimeTaskCreatorManager.class);
        scheduleList=new ArrayList<TimerTaskSchedule>();
    }

    public void envChanged() {
        reGenerateTimeTasks();
    }

    public void reGenerateTimeTasks() {
        clear();
        resetTimeTasks();
    }

    private void clear() {
        biCubeTimeTaskCreatorProvider.removeAllTimeTasks(biUser.getUserId());
        scheduleList.clear();
    }

    private void resetTimeTasks() {
        synchronized (TimerRunner.class) {
            scheduleList = getTimerTaskSchedules();
            biCubeTimeTaskCreatorProvider.reGenerateTimeTasks(biUser.getUserId(), scheduleList);
        }
    }

    private List<TimerTaskSchedule> getTimerTaskSchedules() {
        Map<String, UpdateSettingSource> allTimeTaskMap = BIConfigureManagerCenter.getUpdateFrequencyManager().getUpdateSettings(biUser.getUserId());
        List<TimerTaskSchedule> allSchedules = TimerScheduleAdapter.convertSchedule(this.biUser.getUserId(), allTimeTaskMap);
        for (TimerTaskSchedule taskSchedule : allSchedules) {

        }
        return allSchedules;
    }
}
