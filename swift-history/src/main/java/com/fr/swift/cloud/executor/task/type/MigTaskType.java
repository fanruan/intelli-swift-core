package com.fr.swift.cloud.executor.task.type;

import com.fr.swift.cloud.executor.type.ExecutorTaskType;

/**
 * @author Heng.J
 * @date 2020/12/21
 * @description
 * @since swift-1.2.0
 */
public enum MigTaskType implements ExecutorTaskType {
    // 迁移准备任务
    MIGRATE_SCHEDULE,
    // 迁移执行任务
    MIGRATE_TRIGGER,
    // 优先执行index冲突任务
    CLEAR_CONFLICT,
}
