package com.fr.bi.cal.generate.timerTask.quartz;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

import java.util.Date;

/**
 * Created by wuk on 16/6/29.
 */

public class JobTask implements Job {

    public JobTask() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        long userId = Long.valueOf(data.get("userId").toString());
        String jobName = data.getString("jobName");

        CubeBuildStuff cubeBuildStuff= (CubeBuildStuff) data.get("CubeBuildStuff");
        String message = "开始执行cube定时更新任务!当前时间为:" + new Date() + "\n 当前任务为：" + jobName + "\n当前用户ID：" + userId+"\n";
        BILogger.getLogger().info(message);
        System.out.println(message);
        CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }
}
