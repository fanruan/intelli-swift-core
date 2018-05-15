package com.fr.swift.schedule;

import java.util.Date;
import java.util.Map;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftScheduleEntity {

    private String taskName;
    private Class jobClass;
    private String cronExpression;
    private Map<String, String> taskParams;
    private Date startTime;
    private Date endTime;

    public SwiftScheduleEntity(String taskName, Class jobClass, String cronExpression, Map<String, String> taskParams, Date startTime, Date endTime) {
        this.taskName = taskName;
        this.jobClass = jobClass;
        this.cronExpression = cronExpression;
        this.taskParams = taskParams;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Class getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class jobClass) {
        this.jobClass = jobClass;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Map<String, String> getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(Map<String, String> taskParams) {
        this.taskParams = taskParams;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
