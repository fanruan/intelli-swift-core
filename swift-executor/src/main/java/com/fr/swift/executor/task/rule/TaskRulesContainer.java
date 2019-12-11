package com.fr.swift.executor.task.rule;

import com.fr.swift.executor.type.ExecutorTaskType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Heng.J
 * @date 2019/12/10
 * @description
 * @since swift 1.1
 */
public class TaskRulesContainer {

    private static TaskRulesContainer INSTANCE = new TaskRulesContainer();

    public static TaskRulesContainer getInstance() {
        return INSTANCE;
    }

    private ConcurrentMap<String, BasicRules> rulesContainer = new ConcurrentHashMap<>();

    public void registerRules(ExecutorTaskType type, BasicRules filterRules) {
        rulesContainer.put(type.name(), filterRules);
    }

    public BasicRules getClassByType(String type) {
        return rulesContainer.get(type);
    }
}
