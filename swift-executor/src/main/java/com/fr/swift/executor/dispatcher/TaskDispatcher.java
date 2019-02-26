package com.fr.swift.executor.dispatcher;

import com.fr.swift.executor.ExecutorManager;
import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;
import com.fr.swift.executor.thread.TaskExecuteRunnable;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.SwiftExecutors;

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

    private static TaskDispatcher INSTANCE = new TaskDispatcher();

    public static TaskDispatcher getInstance() {
        return INSTANCE;
    }

    private static long TASK_PULL_INTERVAL = 10000L;

    private static int EXECUTE_THREAD_NUM;

    private Lock lock = new ReentrantLock();

    private Condition freeCondition = lock.newCondition();

    private TaskDispatcher() {
        EXECUTE_THREAD_NUM = Runtime.getRuntime().availableProcessors();
        Thread dispachTthread = SwiftExecutors.newThread(new DispatchRunnable());
        Thread[] threads = new Thread[EXECUTE_THREAD_NUM];
        for (int i = 0; i < threads.length; i++) {
            String threadName = "TaskExecuteRunnable [" + i + "]";
            Runnable runnable = new TaskExecuteRunnable(dispachTthread, threadName, lock, freeCondition);
            Thread thread = SwiftExecutors.newThread(runnable, threadName);
            threads[i] = thread;
            thread.start();
        }
        dispachTthread.start();
    }

    private class DispatchRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    try {
                        if (isThreadBusy()) {
                            freeCondition.await();
                        }
                    } finally {
                        lock.unlock();
                    }
                    ExecutorTask pickedTask = TaskRouter.getInstance().pickExecutorTask(lock);
                    if (pickedTask == null) {
                        boolean hasPolled = ExecutorManager.getInstance().pull();
                        if (!hasPolled) {
                            Thread.sleep(TASK_PULL_INTERVAL);
                        }
                    } else {
                        ConsumeQueue.getInstance().offer(pickedTask);
                    }
                } catch (InterruptedException e) {
                    SwiftLoggers.getLogger().error(e);
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