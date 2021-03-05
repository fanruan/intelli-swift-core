package com.fr.swift.cloud.executor.task.job;

import java.util.concurrent.RunnableFuture;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public interface IExecutorJob extends RunnableFuture {

    @Override
    void run();
}
