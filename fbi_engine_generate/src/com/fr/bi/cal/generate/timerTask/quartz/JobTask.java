package com.fr.bi.cal.generate.timerTask.quartz;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.cal.generate.timerTask.adapter.TimerScheduleAdapter;
import com.fr.bi.stable.constant.DBConstant;
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
        String sourceName=data.getString("sourceName");
        boolean tableExisted = null == TimerScheduleAdapter.tableCheck(userId, sourceName);
        if (!tableExisted&& !DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE.equals(sourceName)){
            return;
        }

        CubeBuildStuff cubeBuildStuff= (CubeBuildStuff) data.get("CubeBuildStuff");
        String message = "timerTask started!Current time is:" + new Date() + "\n Current task：" + jobName + "\nCurrent User：" + userId+"\n";
        BILogger.getLogger().info(message);
        CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }
}
