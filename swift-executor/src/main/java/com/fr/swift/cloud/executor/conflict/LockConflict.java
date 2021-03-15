package com.fr.swift.cloud.executor.conflict;

import com.fr.swift.cloud.executor.task.ExecutorTask;

/**
 * @Author: Bellman
 * @Date: 2019/9/12 3:20 下午
 */
public interface LockConflict {
    /**
     * 当前约束是否与给定任务冲突
     *
     * @param other
     * @return
     */
    boolean isConflict(ExecutorTask other);

    /**
     * 在每次循环开始之前需要执行，以初始化计数器
     */
    void initCheck();

    /**
     * 为了避免每次遍历约束列表，在开始判断前先筛选出相关的约束，这是判定是否相关接口
     *
     * @param task
     * @return
     */
    boolean isRelatedConflict(ExecutorTask task);
}
