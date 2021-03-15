package com.fr.swift.cloud.executor.task.bean;

import com.fr.swift.cloud.executor.task.info.PlanningInfo;

/**
 * @author Heng.J
 * @date 2020/10/28
 * @description
 * @since swift-1.2.0
 */
public class PlanningBean {

    private PlanningInfo taskInfo;

    public PlanningBean() {
    }

    public PlanningBean(PlanningInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    public PlanningInfo getTaskInfo() {
        return taskInfo;
    }

    @Override
    public String toString() {
        return "PlanningBean{" +
                "taskInfo='" + getTaskInfo() + '\'' +
                '}';
    }
}
