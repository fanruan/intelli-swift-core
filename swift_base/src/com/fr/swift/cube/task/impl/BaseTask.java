package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskStatusChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2017/12/28
 */
abstract class BaseTask implements Task {
    TaskKey key;

    volatile Status status = Status.WAITING;
    private List<TaskStatusChangeListener> listeners = new ArrayList<TaskStatusChangeListener>(1);

    Result result;

    BaseTask(TaskKey key) {
        this.key = key;
    }

    @Override
    public TaskKey key() {
        return key;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        if (this.status == status) {
            return;
        }

        Status prev = this.status;
        this.status = status;

        for (TaskStatusChangeListener listener : listeners) {
            listener.onChange(prev, status);
        }
    }

    @Override
    public void addStatusChangeListener(TaskStatusChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public Result result() {
        return result;
    }

    @Override
    public String toString() {
        return "{" + key + ", " + status + ", " + result + "}";
    }
}