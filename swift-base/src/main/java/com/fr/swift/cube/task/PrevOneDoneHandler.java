package com.fr.swift.cube.task;

/**
 * @author anchore
 * @date 2017/12/19
 */
public interface PrevOneDoneHandler {
    /**
     * 前置任务完成时如何处理本任务
     * <p>
     * 可能产生竞争条件：多个前置任务同时做完时，并发调用这个
     *
     * @param selfKey        本任务
     * @param prevDoneOneKey 完成的前置任务
     */
    void handle(TaskKey selfKey, TaskKey prevDoneOneKey);

}