package com.fr.swift.executor.task;

import com.fr.swift.executor.type.ExecutorTaskType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2019/3/7
 *
 * @author Lucifer
 * @description
 */
public class ExecutorTypeContainer {

    private static ExecutorTypeContainer INSTANCE = new ExecutorTypeContainer();

    public static ExecutorTypeContainer getInstance() {
        return INSTANCE;
    }

    Map<ExecutorTaskType, Class<? extends ExecutorTask>> executorTaskTypeClassMap = new ConcurrentHashMap<ExecutorTaskType, Class<? extends ExecutorTask>>();

    private ExecutorTypeContainer() {
    }

    public void registerClass(ExecutorTaskType type, Class<? extends ExecutorTask> clazz) {
        executorTaskTypeClassMap.put(type, clazz);
    }

    public Class<? extends ExecutorTask> getClassByType(ExecutorTaskType type) {
        return executorTaskTypeClassMap.get(type);
    }
}
