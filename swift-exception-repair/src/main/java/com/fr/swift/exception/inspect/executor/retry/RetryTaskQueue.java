package com.fr.swift.exception.inspect.executor.retry;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 10/15/2019
 */
public interface RetryTaskQueue {

    boolean offer(RetryTask task);

    RetryTask take() throws InterruptedException;

    int getTaskCount();

}
