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
public class TaskRuleContainer {

    private static TaskRuleContainer INSTANCE = new TaskRuleContainer();

    public static TaskRuleContainer getInstance() {
        return INSTANCE;
    }

    private ConcurrentMap<String, TaskRule> rulesContainer = new ConcurrentHashMap<>();

    public void registerRules(ExecutorTaskType type, TaskRule filterRules) {
        rulesContainer.put(type.name(), filterRules);
    }

    public TaskRule getRulesByType(ExecutorTaskType type) {
        return rulesContainer.get(type.name());
    }
}
