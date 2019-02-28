package com.fr.swift.executor.task.job;

/**
 * @author anchore
 * @date 2019/2/27
 */
public abstract class BaseJob<T> implements Job<T> {
    private JobListener jobListener;

    public BaseJob() {
    }

    public BaseJob(JobListener jobListener) {
        this.jobListener = jobListener;
    }

    @Override
    public JobListener getJobListener() {
        return jobListener;
    }
}