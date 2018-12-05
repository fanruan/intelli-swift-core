package com.fr.swift.task.impl;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.task.Task;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.TaskStatusChangeListener;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2017/12/28
 */
abstract class BaseTask implements Task {
    final TaskKey key;

    volatile Status status = Status.WAITING;
    volatile TaskResult result;
    volatile Long start, end;
    private List<TaskStatusChangeListener> listeners = new ArrayList<TaskStatusChangeListener>(1);

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

        SwiftLoggers.getLogger().info("{} {} {}", key, status, result == null ? Strings.EMPTY : result);

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
    public TaskResult result() {
        return result;
    }

    @Override
    public Long getStartTime() {
        return start;
    }

    @Override
    public Long getEndTime() {
        return end;
    }

    @Override
    public Long getCostTime() {
        if (start == null) {
            return null;
        }
        if (end == null) {
            return System.currentTimeMillis() - start;
        }
        return end - start;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, %s}", key, status, result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Task) {
            return ((Task) obj).key().equals(key);
        }
        return false;
    }
}