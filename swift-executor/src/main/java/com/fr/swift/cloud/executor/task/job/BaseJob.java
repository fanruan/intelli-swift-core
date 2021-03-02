package com.fr.swift.cloud.executor.task.job;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author anchore
 * @date 2019/2/27
 */
public abstract class BaseJob<T, P> implements Job<T, P> {
    @JsonIgnore
    protected JobListener jobListener;

    public BaseJob() {
    }

    public BaseJob(JobListener jobListener) {
        this.jobListener = jobListener;
    }

    @Override
    public JobListener getJobListener() {
        return jobListener;
    }

    @Override
    public void setJobListener(JobListener listener) {
        this.jobListener = listener;
    }

    @Override
    public P serializedTag() {
        return null;
    }
}