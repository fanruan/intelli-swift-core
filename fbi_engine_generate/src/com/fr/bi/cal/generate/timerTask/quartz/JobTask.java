package com.fr.bi.cal.generate.timerTask.quartz;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

import java.util.Date;

/**
 * Created by kary on 16/6/29.
 */

public class JobTask implements Job {

    public JobTask() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        long userId = Long.valueOf(data.get("userId").toString());
        String jobName = data.getString("jobName");
        CubeBuild cubeBuild = (CubeBuild) data.get("CubeBuild");
        String message = "timerTask started!Current time is:" + new Date() + "\n Current task：" + jobName + "\nCurrent User：" + userId + "\n";
        BILoggerFactory.getLogger().info(message);
        boolean conditionsMeet = cubeBuild.preConditionsCheck();
        conditionsMeet=false;
        if (!conditionsMeet) {
            String errorMessage = "preConditions check failed! Please check the available HD space and data connections";
            BILoggerFactory.getLogger().error(errorMessage);
            BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), errorMessage, userId);
            BIConfigureManagerCenter.getLogManager().logEnd(userId);
            return;
        }
        CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(new BIUser(userId), cubeBuild), userId);
    }
}
