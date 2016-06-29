package com.fr.bi.cal.generate.timerTask;

import com.fr.bi.cal.generate.timerTask.quartz.JobTask;
import com.fr.bi.cal.generate.timerTask.quartz.QuartzManager;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.jsqlparser.parser.ParseException;
import com.fr.third.org.quartz.SchedulerException;

import java.util.List;

/**
 * Created by Kary on 2016/6/28.
 */
public class BICubeTimeTaskCreatorManager implements BICubeTimeTaskCreatorProvider {
    public BICubeTimeTaskCreatorManager() {
    }

    @Override
    public void reGenerateTimeTasks(long userId, List<TimerTaskSchedule> scheduleList) {
        for (TimerTaskSchedule schedule : scheduleList) {
            JobTask jobTask = new JobTask(schedule.getCubeBuildStuff(), userId);
            try {
                QuartzManager.addJob(schedule.getJobName(), jobTask, schedule.getTimeSchedule()); //每2秒钟执行一次
            } catch (SchedulerException e) {
                throw BINonValueUtils.beyondControl(e);
            } catch (ParseException e) {
                throw BINonValueUtils.beyondControl(e);
            } catch (java.text.ParseException e) {
                throw BINonValueUtils.beyondControl(e);
            }
        }
    }
    

    @Override
    public void removeAllTimeTasks(long userId, List<TimerTaskSchedule> scheduleList) {
        for (TimerTaskSchedule schedule : scheduleList) {
            try {
                QuartzManager.removeJob(schedule.getJobName());
            } catch (SchedulerException e) {
                throw BINonValueUtils.beyondControl();
            }
        }

        }


}
