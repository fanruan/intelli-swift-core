package com.fr.bi.cal.generate.timerTask.quartz;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.third.org.apache.poi.hssf.record.formula.functions.Date;
import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

/**
 * Created by wuk on 16/6/29.
 */
public class JobTask implements Job {
    private CubeBuildStuff cubeBuildStuff;
    private long userId;
    public JobTask(CubeBuildStuff cubeBuildStuff, long userId) {
        this.userId=userId;
        this.cubeBuildStuff=cubeBuildStuff;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("开始执行cube定时更新任务!当前时间为:"+new Date());
        CubeGenerationManager.getCubeManager().addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }
}
