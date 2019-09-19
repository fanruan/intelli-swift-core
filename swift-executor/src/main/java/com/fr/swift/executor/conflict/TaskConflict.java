package com.fr.swift.executor.conflict;

import com.fr.swift.executor.task.ExecutorTask;

import java.util.List;

/**
 * @Author: Bellman
 * @Date: 2019/9/12 11:34 上午
 */
public interface TaskConflict {
    /**
     * 判定当前 task 是否与队列中的 task 冲突
     *
     * @param executorTask 想要插入队列的任务
     * @param InQueueTasks 队列中的全部任务
     * @return
     */
    boolean isConflict(ExecutorTask executorTask, List<ExecutorTask> InQueueTasks);

    void initVirtualLocks(List<ExecutorTask> InQueueTasks);

    void finishVirtualLocks();
}
