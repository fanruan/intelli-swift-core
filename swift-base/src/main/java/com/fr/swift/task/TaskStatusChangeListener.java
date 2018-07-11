package com.fr.swift.task;

import com.fr.swift.task.Task.Status;

/**
 * @author anchore
 * @date 2017/12/25
 */
public interface TaskStatusChangeListener {
    void onChange(Status prev, Status now);
}