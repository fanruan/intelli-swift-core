package com.fr.swift.exception.queue;

import com.fr.swift.exception.ExceptionInfo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Marvin
 * @date 8/8/2019
 * @description
 * @since swift 1.1
 */
public class SlaveExceptionInfoQueue implements ExceptionInfoQueue {

    private static final SlaveExceptionInfoQueue INSTANCE = new SlaveExceptionInfoQueue();

    public static SlaveExceptionInfoQueue getInstance() {
        return INSTANCE;
    }

    private BlockingQueue<ExceptionInfo> queue = new ArrayBlockingQueue<>(10000);

    private SlaveExceptionInfoQueue() {
    }

    @Override
    public boolean offer(ExceptionInfo info) {
        return queue.offer(info);
    }

    @Override
    public ExceptionInfo take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void initExceptionInfoQueue() {
        //slave队列初始化时会找出sourceNodeId和operateNodeId均为本节点id且State为PENDING的异常信息加入队列
    }
}