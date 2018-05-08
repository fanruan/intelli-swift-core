package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.TaskKey;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/1/12
 */
public class LocalTaskPool extends BaseTaskPool<LocalTask> {
    private static final LocalTaskPool INSTANCE = new LocalTaskPool();

    public static LocalTaskPool getInstance() {
        return INSTANCE;
    }

    private LocalTaskPool() {
        tasks = new ConcurrentHashMap<TaskKey, LocalTask>();
    }
}
