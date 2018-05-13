package com.fr.swift.cube.task;

/**
 * @author anchore
 * @date 2017/12/28
 */
public interface Task {
    TaskKey key();

    Status status();

    void setStatus(Status status);

    void addStatusChangeListener(TaskStatusChangeListener listener);

    TaskResult result();

    Long getStartTime();

    Long getEndTime();

    Long getCostTime();

    /**
     * @author anchore
     * @date 2017/12/15
     */
    enum Status {
        // 等待所有前置任务完成
        WAITING(0),
        // 可执行，但没在执行
        RUNNABLE(1),
        // 正在执行
        RUNNING(2),
        // 完成
        DONE(3);

        public int order() {
            return order;
        }

        private final int order;

        Status(int order) {
            this.order = order;
        }
    }

}