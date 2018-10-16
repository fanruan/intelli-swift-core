package com.fr.swift.task.service;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * This class created on 2018/7/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServiceTaskExecutor implements ServiceTaskExecutor {

    private final static long SLEEP_TIME = 10L;
    private final BlockingQueue<ServiceCallable> callableQueue = new LinkedBlockingQueue<ServiceCallable>(10000);
    private final Map<SourceKey, ServiceCallable> runningCallables = new ConcurrentHashMap<SourceKey, ServiceCallable>();
    private ServiceTaskFetcher fetcher;

    public SwiftServiceTaskExecutor(int threadNum) {
        fetcher = new ServiceTaskFetcher(threadNum);
        SwiftExecutors.newThread(fetcher).start();
    }

    @Override
    public void submit(ServiceCallable task) throws InterruptedException {
        callableQueue.put(task.addListener(fetcher.serviceTaskListener));
    }

    private class ServiceTaskFetcher implements Runnable {
        Semaphore semaphore;
        ServiceTaskListener serviceTaskListener = new ServiceTaskListener() {
            @Override
            public void handlerEvent(ServiceCallable serviceCallable) {
                if (serviceCallable.getType().isEdit()) {
                    runningCallables.remove(serviceCallable.getKey());
                }
                semaphore.release();
            }
        };
        private ExecutorService executorService;

        ServiceTaskFetcher(int threadNum) {
            executorService = SwiftExecutors.newFixedThreadPool(threadNum, new PoolThreadFactory(SwiftServiceTaskExecutor.class));
            semaphore = new Semaphore(threadNum);
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    semaphore.acquire();
                    ServiceCallable serviceCallable = callableQueue.take();
                    synchronized (ServiceTaskFetcher.class) {
                        if (serviceCallable.getType().isEdit()) {
                            if (runningCallables.containsKey(serviceCallable.getKey())) {
                                //todo 暂时加到队列尾
                                callableQueue.put(serviceCallable);
                                //fixme
                                Thread.sleep(SLEEP_TIME);
                                semaphore.release();
                                continue;
                            } else {
                                runningCallables.put(serviceCallable.getKey(), serviceCallable);
                            }
                        }
                        executorService.submit(serviceCallable);
                    }
                    //todo future处理
                } catch (InterruptedException e) {
                    SwiftLoggers.getLogger().error(e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
