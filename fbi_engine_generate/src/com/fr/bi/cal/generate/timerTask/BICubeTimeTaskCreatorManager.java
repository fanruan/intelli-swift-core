package com.fr.bi.cal.generate.timerTask;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.generate.timerTask.quartz.JobTask;
import com.fr.bi.cal.generate.timerTask.quartz.QuartzManager;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.third.org.quartz.SchedulerException;

import java.util.List;

/**
 * Created by Kary on 2016/6/28.
 */
public class BICubeTimeTaskCreatorManager implements BICubeTimeTaskCreatorProvider {
    private static final BILogger LOGGER = BILoggerFactory.getLogger(BICubeTimeTaskCreatorManager.class);
    public BICubeTimeTaskCreatorManager() {
    }

    @Override
    public void reGenerateTimeTasks(long userId, List<TimerTaskSchedule> scheduleList) {
//        LOGGER.info("***************timer task settings start*****************");
        for (TimerTaskSchedule schedule : scheduleList) {
            JobTask jobTask = new JobTask();
            try {
                QuartzManager.addJob(jobTask, schedule);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
//        LOGGER.info("***************timer task settings end:*****************");
    }

    @Override
    public void removeAllTimeTasks(long userId) {
        try {
//            LOGGER.info("***************timer task clear start*****************");
            QuartzManager.removeAllJobs();
//            LOGGER.info("***************timer task clear end*****************");
        } catch (SchedulerException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


}
