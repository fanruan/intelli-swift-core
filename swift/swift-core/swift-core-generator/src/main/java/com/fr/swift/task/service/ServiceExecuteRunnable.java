package com.fr.swift.task.service;

import com.fr.swift.log.SwiftLoggers;

/**
 * This class created on 2018/11/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 * 单线程对应单队列，即某个sourcekey的多个task只能存在一个队列中
 */
public class ServiceExecuteRunnable implements Runnable {

    private ServiceBlockingQueue serviceBlockingQueue;

    private ServiceTaskExecutor taskExecutor;

    private final String threadName;

    public ServiceExecuteRunnable(String threadName, ServiceTaskExecutor serviceTaskExecutor) {
        serviceBlockingQueue = new SwiftServiceBlockingQueue(1000);
        taskExecutor = serviceTaskExecutor;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        taskExecutor.registerQueue(this.threadName, serviceBlockingQueue);
        while (!Thread.interrupted()) {
            try {
                ServiceCallable serviceCallable = serviceBlockingQueue.take();
                try {
                    serviceCallable.run();
                } finally {
                    serviceBlockingQueue.decreaseNumBySourceKey(serviceCallable.getKey());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                SwiftLoggers.getLogger().error(e);
            } catch (Throwable e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }
}
