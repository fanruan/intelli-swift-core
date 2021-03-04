package com.fr.swift.cloud.executor.task;

import com.fr.swift.cloud.executor.type.ExecutorTaskType;

import java.util.ArrayList;
import java.util.List;
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

    Map<String, Class<? extends ExecutorTask>> executorTaskTypeClassMap = new ConcurrentHashMap<String, Class<? extends ExecutorTask>>();

    private List<ExecutorTaskType> executorTaskTypeList;

    private ExecutorTypeContainer() {
        executorTaskTypeList = new ArrayList<ExecutorTaskType>();
    }

    public void registerClass(ExecutorTaskType type, Class<? extends ExecutorTask> clazz) {
        executorTaskTypeList.add(type);
        executorTaskTypeClassMap.put(type.name(), clazz);
    }

    public Class<? extends ExecutorTask> getClassByType(String type) {
        return executorTaskTypeClassMap.get(type);
    }

    public List<ExecutorTaskType> getExecutorTaskTypeList() {
        return new ArrayList<ExecutorTaskType>(executorTaskTypeList);
    }
}