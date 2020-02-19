package com.fr.swift.executor.task.rule;

import com.fr.swift.executor.task.ExecutorTask;

/**
 * @author Heng.J
 * @date 2019/12/9
 * @description 任务的规则
 * @since swift 1.1
 */
public interface TaskRule {
    /**
     * 重复任务判定规则过滤
     *
     * @param executorTask
     * @return
     * @throws Exception
     */
    boolean isRulesFiltered(ExecutorTask executorTask) throws Exception;
}