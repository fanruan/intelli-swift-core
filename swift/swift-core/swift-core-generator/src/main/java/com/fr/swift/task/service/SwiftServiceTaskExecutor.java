package com.fr.swift.task.service;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class created on 2018/7/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "serviceTaskExecutor")
public class SwiftServiceTaskExecutor implements ServiceTaskExecutor {

    private final BlockingQueue<ServiceCallable> callableQueue = new LinkedBlockingQueue<ServiceCallable>(10000);
    private ServiceTaskFetcher fetcher;

    private Map<String, ServiceBlockingQueue> serviceBlockingQueueMap = new ConcurrentHashMap<String, ServiceBlockingQueue>();

    private static int THREAD_NUM = 10;

    public SwiftServiceTaskExecutor() {
        fetcher = new ServiceTaskFetcher();
        SwiftExecutors.newThread(fetcher).start();

        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < THREAD_NUM; i++) {
            String threadName = "ServiceExecuteRunnable" + i;
            Thread thread = SwiftExecutors.newThread(new ServiceExecuteRunnable(threadName, this), threadName);
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.start();
        }
    }

    @Override
    public <T> Future<T> submit(ServiceCallable<T> task) throws InterruptedException {
        callableQueue.put(task);
        return task;
    }

    @Override
    public void registerQueue(String name, ServiceBlockingQueue queue) {
        serviceBlockingQueueMap.put(name, queue);
    }

    @Override
    public void unRegisterQueue(String name) {
        serviceBlockingQueueMap.remove(name);
    }

    private class ServiceTaskFetcher implements Runnable {

        private ServiceTaskFetcher() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ServiceCallable serviceCallable = callableQueue.take();
                    submitServiceCallable(serviceCallable);
                } catch (Throwable e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }

        private synchronized void submitServiceCallable(ServiceCallable serviceCallable) throws InterruptedException {
            String threadName = null;
            int minNum = 0;
            for (Map.Entry<String, ServiceBlockingQueue> entry : serviceBlockingQueueMap.entrySet()) {
                Integer num = entry.getValue().getNumBySourceKey(serviceCallable.getKey());
                if (num != null && num > 0) {
                    entry.getValue().put(serviceCallable);
                    return;
                } else {
                    int size = entry.getValue().size();
                    if (threadName == null) {
                        threadName = entry.getKey();
                        minNum = size;
                    } else {
                        if (size <= minNum) {
                            threadName = entry.getKey();
                            minNum = size;
                        }
                    }
                }
            }
            serviceBlockingQueueMap.get(threadName).put(serviceCallable);
        }
    }
}
