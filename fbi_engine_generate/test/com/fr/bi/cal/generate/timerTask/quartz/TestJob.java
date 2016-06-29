package com.fr.bi.cal.generate.timerTask.quartz;

import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

import java.util.Date;



/**
 * Created by Kary on 2016/6/29.
 */
public class TestJob implements Job {

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        // TODO Auto-generated method stub
        System.out.println("开始执行cube定时更新任务!当前时间为:"+new Date());;
    }

}
