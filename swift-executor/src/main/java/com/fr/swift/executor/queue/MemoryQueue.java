package com.fr.swift.executor.queue;

import com.fr.swift.executor.task.ExecutorTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description 代理的内存队列
 */
public final class MemoryQueue {

    private static MemoryQueue INSTANCE = new MemoryQueue();

    private BlockingDeque<ExecutorTask> queue = new LinkedBlockingDeque<ExecutorTask>();

    public static MemoryQueue getInstance() {
        return INSTANCE;
    }

    public List<ExecutorTask> pullBeforeTime(long maxTime) {
        List<ExecutorTask> polledTasks = new ArrayList<ExecutorTask>();
        ExecutorTask task;
        while ((task = queue.peek()) != null) {
            if (task.getCreateTime() <= maxTime) {
                polledTasks.add(task);
                queue.poll();
            } else {
                break;
            }
        }
        return polledTasks;
    }

    public boolean offer(ExecutorTask task) {
        return queue.offer(task);
    }
}
