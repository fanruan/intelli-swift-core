package com.fr.swift.task;

import com.fr.swift.util.function.Predicate;

import java.util.Collection;
import java.util.List;

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

    List<TaskKey> taskOf(Predicate<T> predicate);
}