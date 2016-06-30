package com.fr.bi.cal.generate.timerTask.quartz;

import com.fr.bi.cal.generate.timerTask.TimerTaskSchedule;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by wuk on 16/6/30.
 */
public class QuartzManagerTest extends TestCase {
    public void testBasic() {
    }

    public void testAddJob() throws Exception {
        String jobName = "test";
        TestJob job = new TestJob();
        try {
            TimerTaskSchedule schedule=new TimerTaskSchedule("0/2 * * * * ?",null,jobName,-999);
            QuartzManager.addJob(job,schedule); //每2秒钟执行一次
            Thread.sleep(3000);
            assertTrue(jobName.equals(QuartzCounts.getInstance().getFlag().get("jobName")));
            QuartzManager.removeJob(jobName);
        } catch (Exception e) {
            assertFalse(true);
        }
    }

    @Test
    public void testModifyJobTime() throws Exception {

    }

    @Test
    public void testRemoveJob() throws Exception {
        String jobName = "test";
        TestJob job = new TestJob();
        try {
            TimerTaskSchedule schedule=new TimerTaskSchedule("0/2 * * * * ?",null,jobName,-999);
            QuartzManager.addJob(job,schedule);
            assertTrue(QuartzManager.getSf().getScheduler().getJobNames("group").length==1);
            QuartzManager.removeJob(schedule.getJobName());
            assertTrue(QuartzManager.getSf().getScheduler().getJobNames("group").length== 0);
        } catch (Exception e) {
            assertFalse(true);
        }
    }

}
