package com.fr.swift.executor.task.job;

import java.util.concurrent.FutureTask;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public class ExecutorJob extends FutureTask implements IExecutorJob {

    public ExecutorJob(Job job) {
        super(job);
    }

    public void jobSuccess() {

    }

    public void jobFailure() {

    }
}
