package com.fr.swift.exception.inspect.executor.retry;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 10/14/2019
 */
public class RepositoryRetryTaskQueue implements RetryTaskQueue {

    private static final RepositoryRetryTaskQueue INSTANCE = new RepositoryRetryTaskQueue();

    private BlockingQueue<RetryTask> queue = new ArrayBlockingQueue<>(10000);

    private RepositoryRetryTaskQueue() {
    }

    public static RepositoryRetryTaskQueue getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean offer(RetryTask task) {
        return queue.offer(task);
    }

    @Override
    public RetryTask take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public int getTaskCount() {
        return queue.size();
    }

}
