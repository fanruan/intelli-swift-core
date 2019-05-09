package com.fr.swift.executor.task;

import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.executor.type.StatusType;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * This class created on 2019/2/13
 *
 * @author Lucifer
 * @description TaskRouter的操作需要加全局锁，避免一边读一边写
 */
public class TaskRouter {

    private List<ExecutorTask> idleTasks;

    private static TaskRouter INSTANCE = new TaskRouter();

    public static TaskRouter getInstance() {
        return INSTANCE;
    }

    private TaskRouter() {
        this.idleTasks = new ArrayList<ExecutorTask>();
    }

    public synchronized boolean addTasks(List<ExecutorTask> taskList) {
        List<ExecutorTask> newTaskList = new ArrayList<ExecutorTask>(taskList);
        Collections.sort(newTaskList, new TaskTimeComparator());
        this.idleTasks.addAll(newTaskList);
        return true;
    }

    public List<ExecutorTask> getIdleTasks() {
        return idleTasks;
    }

    /**
     * 移除task并修改内存中执行状态
     *
     * @param task
     * @return
     */
    public boolean remove(ExecutorTask task) {
        task.setStatusType(StatusType.RUNNING);
        return idleTasks.remove(task);
    }

    public synchronized ExecutorTask pickExecutorTask(Lock lock) {
        List<ExecutorTask> idleTasks = TaskRouter.getInstance().getIdleTasks();
        ExecutorTask taskPicked = null;
        for (ExecutorTask idleTask : idleTasks) {
            if (isQualified(idleTask, lock)) {
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

    public synchronized void clear() {
        idleTasks.clear();
    }

    private boolean isQualified(ExecutorTask task, Lock lock) {
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
                    return !LockType.isVirtualLock(executorTask);
                case REAL_SEG://虚拟锁任务可以执行；不是同一块的真实锁任务可以执行
                    if (LockType.isVirtualLock(executorTask)) {
                        return false;
                    }
                    return !LockType.isRealLock(executorTask) || LockType.isSameLockKey(runningTask, executorTask);
                case VIRTUAL_SEG://表锁、真实锁可以执行
                    if (LockType.isTableLock(executorTask)) {
                        return false;
                    }
                    return !LockType.isRealLock(executorTask);
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 根据task的createTime排序
     */
    private class TaskTimeComparator implements Comparator<ExecutorTask> {

        @Override
        public int compare(ExecutorTask task1, ExecutorTask task2) {
            if (task1.getCreateTime() >= task2.getCreateTime()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}