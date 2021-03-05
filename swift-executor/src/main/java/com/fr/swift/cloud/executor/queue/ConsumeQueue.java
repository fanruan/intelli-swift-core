package com.fr.swift.cloud.executor.queue;

import com.fr.swift.cloud.executor.task.ExecutorTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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

    private LinkedBlockingQueue<ExecutorTask> queue = new LinkedBlockingQueue<>();

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
        return new ArrayList<>(taskList);
    }

    public int size() {
        return taskList.size();
    }
}
