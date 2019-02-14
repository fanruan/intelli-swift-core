package com.fr.swift.task;

import java.util.List;

/**
 * @author anchore
 * @date 2017/12/28
 * <p>
 * 管理节点 上的任务描述，只关心任务的依赖执行情况
 * 不关心任务具体的执行，具体的执行在WorkerTask里做
 */
public interface SchedulerTask extends Task {
    /**
     * 这里要与执行节点通信，取消任务
     */
    void cancel();

    /**
     * 这里要与执行节点通信，发起任务
     */
    void triggerRun();

    /**
     * 执行节点执行完任务，回调的
     *
     * @param result 结果
     */
    void onDone(TaskResult result);

    void addPrev(SchedulerTask prev);

    void addNext(SchedulerTask next);

    List<SchedulerTask> prevAll();

    List<SchedulerTask> nextAll();
}