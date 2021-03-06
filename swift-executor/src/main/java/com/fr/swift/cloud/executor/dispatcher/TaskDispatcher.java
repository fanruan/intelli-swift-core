package com.fr.swift.cloud.executor.dispatcher;

import com.fr.swift.cloud.executor.ExecutorManager;
import com.fr.swift.cloud.executor.queue.ConsumeQueue;
import com.fr.swift.cloud.executor.task.ExecutorTask;
import com.fr.swift.cloud.executor.task.TaskRouter;
import com.fr.swift.cloud.executor.thread.TaskExecuteRunnable;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.util.concurrent.SwiftExecutors;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2019/2/13
 *
 * @author Lucifer
 * @description
 */
public class TaskDispatcher {

    private static volatile TaskDispatcher INSTANCE = null;

    public static TaskDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (TaskDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    private static long TASK_PULL_INTERVAL = TimeUnit.SECONDS.toMillis(10);

    private static int EXECUTE_THREAD_NUM;

    private Lock executorLock = new ReentrantLock();

    private Condition freeCondition = executorLock.newCondition();

    private Thread dispachTthread;

    private Thread[] threads;

    private TaskDispatcher() {
        ExecutorManager.getInstance().pullDBTask();
        EXECUTE_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;
        threads = new Thread[EXECUTE_THREAD_NUM];
        for (int i = 0; i < threads.length; i++) {
            String threadName = "TaskExecuteRunnable [" + i + "]";
            Runnable runnable = new TaskExecuteRunnable(dispachTthread, threadName, executorLock, freeCondition);
            Thread thread = SwiftExecutors.newThread(runnable, threadName);
            threads[i] = thread;
            thread.start();
        }
        dispachTthread = SwiftExecutors.newThread(new DispatchRunnable());
        dispachTthread.start();
    }

    public void stop() throws InterruptedException {
        dispachTthread.interrupt();
        while (!ConsumeQueue.getInstance().getTaskList().isEmpty()) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        }
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private class DispatchRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    executorLock.lock();
                    try {
                        if (isThreadBusy()) {
                            freeCondition.await();
                        }
                    } finally {
                        executorLock.unlock();
                    }
                    ExecutorTask pickedTask = TaskRouter.getInstance().pickExecutorTask(executorLock);
                    if (pickedTask == null) {
                        boolean hasPolled = ExecutorManager.getInstance().pullMemTask();
                        if (!hasPolled) {
                            executorLock.lock();
                            try {
                                freeCondition.await(TASK_PULL_INTERVAL, TimeUnit.MILLISECONDS);
                            } finally {
                                executorLock.unlock();
                            }
                        }
                    } else {
                        ConsumeQueue.getInstance().offer(pickedTask);
                    }
                } catch (InterruptedException e) {
                    SwiftLoggers.getLogger().error(e);
                    break;
                } catch (Throwable throwable) {
                    SwiftLoggers.getLogger().error(throwable);
                }
            }
        }

    }

    private boolean isThreadBusy() {
        return ConsumeQueue.getInstance().size() >= EXECUTE_THREAD_NUM;
    }
}