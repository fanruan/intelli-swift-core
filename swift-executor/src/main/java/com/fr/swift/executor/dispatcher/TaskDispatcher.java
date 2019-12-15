package com.fr.swift.executor.dispatcher;

import com.fr.swift.executor.ExecutorManager;
import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;
import com.fr.swift.executor.task.rule.TaskRule;
import com.fr.swift.executor.task.rule.TaskRuleContainer;
import com.fr.swift.executor.thread.TaskExecuteRunnable;
import com.fr.swift.executor.type.ExecutorTaskType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.SwiftExecutors;

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

    private static long TASK_PULL_INTERVAL = 10000L;

    private static int EXECUTE_THREAD_NUM;

    private Lock executorLock = new ReentrantLock();

    private Condition freeCondition = executorLock.newCondition();

    private Thread dispachTthread;

    private Thread[] threads;

    private TaskDispatcher() {
        EXECUTE_THREAD_NUM = Runtime.getRuntime().availableProcessors();
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
                    if (pickedTask != null) {
                        try {
                            ExecutorTaskType executorTaskType = pickedTask.getExecutorTaskType();
                            TaskRule rule = TaskRuleContainer.getInstance().getRulesByType(executorTaskType);
                            if (rule != null && rule.isRulesFiltered(pickedTask)) {
                                continue;
                            }
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
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