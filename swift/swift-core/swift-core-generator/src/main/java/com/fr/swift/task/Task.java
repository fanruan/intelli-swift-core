package com.fr.swift.task;

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

        private final int order;

        Status(int order) {
            this.order = order;
        }

        public int order() {
            return order;
        }

        public int compare(Status status) {
            return order - status.order;
        }
    }
}