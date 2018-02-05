package com.fr.swift.cube.task;

import com.fr.swift.cube.task.Task.Status;

/**
 * @author anchore
 * @date 2017/12/25
 */
public interface TaskStatusChangeListener {
    void onChange(Status prev, Status now);
}