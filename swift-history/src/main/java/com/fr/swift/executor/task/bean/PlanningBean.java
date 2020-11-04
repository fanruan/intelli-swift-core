package com.fr.swift.executor.task.bean;

import com.fr.swift.executor.task.bean.info.PlanningInfo;
import com.fr.swift.executor.type.SwiftTaskType;

/**
 * @author Heng.J
 * @date 2020/10/28
 * @description
 * @since swift-1.2.0
 */
public class PlanningBean {

    private PlanningInfo taskInfo;

    private SwiftTaskType taskType;

    public PlanningBean() {
    }

    public PlanningBean(PlanningInfo taskInfo, SwiftTaskType taskType) {
        this.taskInfo = taskInfo;
        this.taskType = taskType;
    }

    public PlanningInfo getTaskInfo() {
        return taskInfo;
    }

    public SwiftTaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return "PlanningBean{" +
                "taskInfo='" + getTaskInfo() + '\'' +
                ", taskType='" + getTaskType() + '\'' +
                '}';
    }
}
