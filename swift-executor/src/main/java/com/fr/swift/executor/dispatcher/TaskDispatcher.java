package com.fr.swift.executor.dispatcher;

import com.fr.swift.executor.ExecutorManager;
import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;
import com.fr.swift.executor.thread.TaskExecuteRunnable;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Util;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.List;
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
                    ExecutorTask pickedTask = pickExecutorTask();
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


    private ExecutorTask pickExecutorTask() {
        synchronized (TaskRouter.class) {
            List<ExecutorTask> idleTasks = TaskRouter.getInstance().getIdleTasks();
            ExecutorTask taskPicked = null;
            for (ExecutorTask idleTask : idleTasks) {
                if (isQualified(idleTask)) {
                    taskPicked = idleTask;
                    break;
                }
            }
            if (taskPicked != null && TaskRouter.getInstance().remove(taskPicked)) {
                return taskPicked;
            } else {
                return null;
            }
        }
    }

    private boolean isThreadBusy() {
        return ConsumeQueue.getInstance().size() >= EXECUTE_THREAD_NUM;
    }

    private boolean isQualified(ExecutorTask task) {
        lock.lock();
        try {
            List<ExecutorTask> taskList = ConsumeQueue.getInstance().getTaskList();
            for (ExecutorTask runningTask : taskList) {
                if (isTasksConfilct(runningTask, task)) {
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 任务是否冲突
     *
     * @param runningTask
     * @param executorTask
     * @return true:冲突，不能同时执行   false:互斥，可以同时执行
     */
    private boolean isTasksConfilct(ExecutorTask runningTask, ExecutorTask executorTask) {
        //表名不同，直接return false
        if (Util.equals(runningTask.getSourceKey(), executorTask.getSourceKey())) {
            //有一个是NONE，则直接return false
            if (LockType.isNoneLock(runningTask) || LockType.isNoneLock(executorTask)) {
                return false;
            }
            switch (runningTask.getLockType()) {
                case TABLE://虚拟锁任务可以执行
                    if (LockType.isVirtualLock(executorTask)) {
                        return false;
                    }
                    return true;
                case REAL_SEG://虚拟锁任务可以执行；不是同一块的真实锁任务可以执行
                    if (LockType.isVirtualLock(executorTask)) {
                        return false;
                    }
                    if (LockType.isRealLock(executorTask) && !LockType.isSameLockKey(runningTask, executorTask)) {
                        return false;
                    }
                    return true;
                case VIRTUAL_SEG://表锁、真实锁可以执行
                    if (LockType.isTableLock(executorTask)) {
                        return false;
                    }
                    if (LockType.isRealLock(executorTask)) {
                        return false;
                    }
                    return true;
                default:
                    return true;
            }
        } else {
            return false;
        }
    }
}