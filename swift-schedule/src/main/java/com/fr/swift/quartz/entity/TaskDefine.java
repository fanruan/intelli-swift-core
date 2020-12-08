package com.fr.swift.quartz.entity;

import com.fr.swift.quartz.execute.ScheduleJob;
import org.quartz.Job;
import org.quartz.JobKey;

import java.util.Map;

/**
 * @author Heng.J
 * @date 2020/5/13
 * @description 一个最简单, 最基本的定时任务
 * @since swift 1.1
 */
public class TaskDefine {

    public static Builder builder() {
        return new Builder();
    }

    private TaskDefine() {
    }

    /**
     * 名字和分组名
     */
    private JobKey jobKey;

    /**
     * 描述(可以定时任务本身的描述,也可以是触发器的)
     */
    private String description;

    /**
     * 执行cron
     */
    private String cronExpression;

    /**
     * 提供数据支持的数据结构
     */
    private Map<?, ?> jobDataMap;

    /**
     * 具体执行逻辑类
     */
    private Class<? extends Job> jobClass;

    public JobKey getJobKey() {
        return jobKey;
    }

    public void setJobKey(JobKey jobKey) {
        this.jobKey = jobKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Map<?, ?> getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(Map<?, ?> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public Class<? extends Job> getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class<? extends ScheduleJob> jobClass) {
        this.jobClass = jobClass;
    }

    public static class Builder {
        private TaskDefine task;

        public Builder() {
            task = new TaskDefine();
        }

        public Builder jobKey(JobKey jobKey) {
            task.setJobKey(jobKey);
            return this;
        }

        public Builder cronExpression(String corn) {
            task.setCronExpression(corn);
            return this;
        }

        public Builder jobClass(Class<? extends ScheduleJob> jobClass) {
            task.setJobClass(jobClass);
            return this;
        }

        public Builder jobData(Map<?, ?> jobDataMap) {
            task.setJobDataMap(jobDataMap);
            return this;
        }

        public Builder description(String description) {
            task.setDescription(description);
            return this;
        }

        public TaskDefine build() {
            return task;
        }
    }
}
