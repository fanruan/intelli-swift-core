package com.fr.bi.cal.generate.timerTask.quartz;

import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;


/**
 * Created by Kary on 2016/6/29.
 */
public class TestJob implements Job {

    public TestJob() {
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        String jobName = data.getString("jobName");
        System.out.println("开始执行定时任务!当前任务为:"+jobName);
            QuartzCounts.getInstance().getFlag().put("jobName",data.get("sourceName").toString());
    }

}
