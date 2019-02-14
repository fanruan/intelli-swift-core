package com.fr.swift.task.impl;

/**
 * @author anchore
 * @date 2018/5/12
 */
class TaskTomb extends BaseTaskTomb {
    private static final TaskTomb TOMB = new TaskTomb();

    private TaskTomb() {
    }

    static TaskTomb getTomb() {
        return TOMB;
    }
}