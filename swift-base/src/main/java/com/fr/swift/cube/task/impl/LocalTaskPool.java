package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.provider.IndexStuffMedium;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/1/12
 */
public class LocalTaskPool extends BaseTaskPool<LocalTask> {
    private static final LocalTaskPool INSTANCE = new LocalTaskPool();

    private Map<String, IndexStuffMedium> indexStuffMediumMap;

    public static LocalTaskPool getInstance() {
        return INSTANCE;
    }

    private LocalTaskPool() {
        tasks = new ConcurrentHashMap<TaskKey, LocalTask>();
        indexStuffMediumMap = new ConcurrentHashMap<String, IndexStuffMedium>();
    }

    public void putIndexStuffMedium(int round, IndexStuffMedium indexStuffMedium) {
        indexStuffMediumMap.put(String.valueOf(round), indexStuffMedium);
    }

    public IndexStuffMedium getIndexStuffMedium(int round) {
        return indexStuffMediumMap.get(String.valueOf(round));
    }
}
