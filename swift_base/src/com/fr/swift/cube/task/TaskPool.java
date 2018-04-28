package com.fr.swift.cube.task;

import com.fr.swift.cube.task.Task.Status;

import java.util.Collection;
import java.util.Set;

/**
 * @author anchore
 * @date 2017/12/15
 */
public interface TaskPool<T extends Task> {
    void add(T task);

    boolean contains(TaskKey key);

    T get(TaskKey key);

    Collection<TaskKey> allTasks();

    void remove(TaskKey key);

    Set<TaskKey> tasksOf(Status status);
}