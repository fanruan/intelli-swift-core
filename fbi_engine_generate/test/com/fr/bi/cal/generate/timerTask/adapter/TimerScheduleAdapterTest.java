package com.fr.bi.cal.generate.timerTask.adapter;

import com.fr.third.org.quartz.SchedulerException;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by kary on 16/6/30.
 * 测试定时能否被正确设置
 */
public class TimerScheduleAdapterTest extends TestCase {


    public void testTimeConvert() {
        String firstDayOfMonth = TimerTaskScheduleTool.getFirstDayOfMonthTask().getTimeSchedule();
        assertTrue(firstDayOfMonth.equals("0 0 10 ? * 1"));
        String FirstHourOfDay = TimerTaskScheduleTool.getFirstHourOfDayTask().getTimeSchedule();
        assertTrue(FirstHourOfDay.equals("0 0 0 ? * 1"));
        String everyMonday = TimerTaskScheduleTool.getEveryMondayTask().getTimeSchedule();
        assertTrue(everyMonday.equals("0 0 2 ? * 1"));

    }

    public void testSettingTimeTasks() throws SchedulerException {
        BICubeTimeTaskCreatorProvider manager = new BICubeTimeTaskCreatorManager();
         List<TimerTaskSchedule> scheduleList = TimerTaskScheduleTool.getAllTasks();
        manager.reGenerateTimeTasks(-999, scheduleList);
        assertTrue(QuartzManager.getSf().getScheduler().getJobNames("group").length == scheduleList.size());
        manager.removeAllTimeTasks(-999);
        assertTrue(QuartzManager.getSf().getScheduler().getJobNames("group").length == 0);
    }
}
