package com.fr.swift.task.impl;

import com.fr.swift.task.Task;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskPool;
import com.fr.swift.util.function.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2017/12/31
 */
abstract class BaseTaskPool<T extends Task> implements TaskPool<T> {
    Map<TaskKey, T> tasks;

    @Override
    public void add(T task) {
        tasks.put(task.key(), task);
    }

    @Override
    public boolean contains(TaskKey key) {
        return tasks.containsKey(key);
    }

    @Override
    public T get(TaskKey key) {
        return tasks.get(key);
    }

    @Override
    public Collection<TaskKey> allTasks() {
        return tasks.keySet();
    }

    @Override
    public void remove(TaskKey key) {
        tasks.remove(key);
    }

    @Override
    public List<TaskKey> taskOf(Predicate<T> predicate) {
        List<TaskKey> specTasks = new ArrayList<TaskKey>();
        for (Entry<TaskKey, T> entry : tasks.entrySet()) {
            if (predicate.test(entry.getValue())) {
                specTasks.add(entry.getKey());
            }
        }
        return specTasks;
    }
}