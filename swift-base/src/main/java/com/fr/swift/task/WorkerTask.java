package com.fr.swift.task;

/**
 * @author anchore
 * @date 2017/12/28
 * <p>
 * 执行节点 上的任务描述，只关心自己完成的情况，通知管理节点
 * 它并不清楚整个任务链的情况，那个是由SchedulerTask掌控
 */
public interface WorkerTask extends Task, Runnable {
    /**
     * 接收管理节点取消任务的通知
     */
    void onCancel();

    /**
     * 任务完成，通知管理节点结果
     *
     * @param result 结果
     */
    void done(TaskResult result);

    /**
     * WorkerTask需要有一个Worker
     * 但是一个Worker不一定要附在WorkerTask上
     */
    interface Worker {
        void setOwner(WorkerTask workerTask);

        /**
         * 具体任务的内容
         */
        void work();

        void workOver(TaskResult result);
    }
}