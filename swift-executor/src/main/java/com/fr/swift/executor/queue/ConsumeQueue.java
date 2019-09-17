package com.fr.swift.executor.queue;

import com.fr.swift.executor.task.ExecutorTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class created on 2019/2/21
 *
 * @author Lucifer
 * @description 供线程消费的任务队列
 */
public class ConsumeQueue {

    private static final int DEFAULT_INITIAL_CAPACITY = 100;
    private static ConsumeQueue INSTANCE = new ConsumeQueue();

    public static ConsumeQueue getInstance() {
        return INSTANCE;
    }

    /**
     * 使用 PriorityBlockingQueue 实现拥有更高优先级(数字更大)的任务优先 take
     */
    private PriorityBlockingQueue<ExecutorTask> queue = new PriorityBlockingQueue<>(DEFAULT_INITIAL_CAPACITY, new Comparator<ExecutorTask>() {
        @Override
        public int compare(ExecutorTask o1, ExecutorTask o2) {
            return Integer.compare(o1.getPriority(), o2.getPriority());
        }
    });

    private List<ExecutorTask> taskList = new ArrayList<>();

    private ConsumeQueue() {
    }

    public boolean offer(ExecutorTask task) {
        if (queue.offer(task)) {
            taskList.add(task);
            return true;
        }
        return false;
    }

    public ExecutorTask take() throws InterruptedException {
        return queue.take();
    }

    public synchronized boolean removeTask(ExecutorTask task) {
        if (task == null) {
            return false;
        }
        return taskList.remove(task);
    }

    public List<ExecutorTask> getTaskList() {
        return taskList;
    }

    public int size() {
        return taskList.size();
    }
}
