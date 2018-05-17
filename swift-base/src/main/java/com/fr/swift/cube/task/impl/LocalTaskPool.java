package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.provider.IndexStuffType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/1/12
 */
public class LocalTaskPool extends BaseTaskPool<LocalTask> {
    private static final LocalTaskPool INSTANCE = new LocalTaskPool();

    private Map<String, IndexStuffType> indexStuffTypeMap;

    public static LocalTaskPool getInstance() {
        return INSTANCE;
    }

    private LocalTaskPool() {
        tasks = new ConcurrentHashMap<TaskKey, LocalTask>();
        indexStuffTypeMap = new ConcurrentHashMap<String, IndexStuffType>();
    }

    public void putIndexStuffType(int round, IndexStuffType indexStuffType) {
        indexStuffTypeMap.put(String.valueOf(round), indexStuffType);
    }

    public IndexStuffType getIndexStuffType(int round) {
        return indexStuffTypeMap.get(String.valueOf(round));
    }
}
