package com.fr.swift.cloud.groovy.schedule;

import com.fr.swift.cloud.quartz.config.ScheduleTaskType;
import com.fr.swift.cloud.quartz.execute.ScheduleJob;
import groovy.lang.GroovyObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/1
 */
@DisallowConcurrentExecution
public class ScheduleGroovyJobWrapper implements ScheduleJob {

    private GroovyObject object;

    public ScheduleGroovyJobWrapper() {
    }

    public ScheduleGroovyJobWrapper(GroovyObject object) {
        this.object = object;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        object = (GroovyObject) context.getJobDetail().getJobDataMap().get("groovyObject");
        object.invokeMethod("execute", context);
    }

    @Override
    public String getCronExpression() {
        return (String) object.invokeMethod("getCronExpression", null);
    }

    @Override
    public ScheduleTaskType getExecutorType() {
        return (ScheduleTaskType) object.invokeMethod("getExecutorType", null);
    }

    @Override
    public JobKey getJobKey() {
        return (JobKey) object.invokeMethod("jobKey", null);
    }
}
