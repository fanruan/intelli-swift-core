package com.fr.swift.task;

/**
 * @author anchore
 * @date 2017/12/8
 */
public interface TaskExecutor {
    /**
     * 加入
     *
     * @param task runnable的task
     */
    void add(WorkerTask task);
}