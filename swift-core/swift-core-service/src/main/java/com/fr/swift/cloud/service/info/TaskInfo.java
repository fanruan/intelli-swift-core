package com.fr.swift.cloud.service.info;

import com.fr.swift.cloud.executor.type.ExecutorTaskType;

/**
 * @author Heng.J
 * @date 2020/11/13
 * @description 计划任务信息, 同planningInfo
 * @since swift-1.2.0
 */
public interface TaskInfo {
    ExecutorTaskType type();
}
