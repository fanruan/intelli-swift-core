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
public class MasterExceptionInfoQueue implements ExceptionInfoQueue {

    private static final MasterExceptionInfoQueue INSTANCE = new MasterExceptionInfoQueue();

    public static MasterExceptionInfoQueue getInstance() {
        return INSTANCE;
    }

    private BlockingQueue<ExceptionInfo> queue = new ArrayBlockingQueue<>(10000);

    private MasterExceptionInfoQueue() {
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
        //master队列初始化时会找出State为UNSOLVED的异常信息加入队列
    }
}