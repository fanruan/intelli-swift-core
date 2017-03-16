package com.fr.bi.cal.generate.timerTask.quartz;

import com.fr.bi.cal.generate.timerTask.adapter.TimerTaskScheduleTool;
import junit.framework.TestCase;

/**
 * Created by kary on 16/6/30.
 * 测试定时能否被正确触发
 */
public class QuartzManagerTest extends TestCase {

    public void testSingleJob() throws Exception {
        TestJob job = new TestJob();
        try {
            QuartzManager.addJob(job, TimerTaskScheduleTool.getSecondsTask()); //每2秒钟执行一次
            Thread.sleep(3000);
            assertTrue(TimerTaskScheduleTool.getJobName().equals(QuartzCounts.getInstance().getFlag().get("jobName")));
            assertTrue(QuartzCounts.getInstance().getFlag().size()>0);
            QuartzManager.removeAllJobs();
            assertTrue(QuartzManager.getSf().getScheduler().getJobNames("group").length== 0);
        } catch (Exception e) {
            assertFalse(true);
        }
    }
}
