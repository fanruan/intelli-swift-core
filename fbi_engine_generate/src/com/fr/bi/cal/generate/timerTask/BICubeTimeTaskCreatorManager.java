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
    private static final BILogger logger = BILoggerFactory.getLogger(BICubeTimeTaskCreatorManager.class);
    public BICubeTimeTaskCreatorManager() {
    }

    @Override
    public void reGenerateTimeTasks(long userId, List<TimerTaskSchedule> scheduleList) {
        logger.info("***************timer task settings start*****************");
        for (TimerTaskSchedule schedule : scheduleList) {
            JobTask jobTask = new JobTask();
            try {
                QuartzManager.addJob(jobTask, schedule);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        logger.info("***************timer task settings end:*****************");
    }

    @Override
    public void removeAllTimeTasks(long userId) {
        try {
            logger.info("***************timer task clearAnalysisETLCache start*****************");
            QuartzManager.removeAllJobs();
            logger.info("***************timer task clearAnalysisETLCache end*****************");
        } catch (SchedulerException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


}
