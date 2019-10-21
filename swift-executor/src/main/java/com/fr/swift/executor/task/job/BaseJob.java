package com.fr.swift.executor.task.job;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author anchore
 * @date 2019/2/27
 */
public abstract class BaseJob<T, P> implements Job<T, P> {
    @JsonIgnore
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

    @Override
    public P serializedTag() {
        return null;
    }
}