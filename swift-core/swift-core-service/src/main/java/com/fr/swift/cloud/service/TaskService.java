package com.fr.swift.cloud.service;

import com.fr.swift.cloud.service.info.TaskInfo;

/**
 * @author Heng.J
 * @date 2020/11/13
 * @description
 * @since swift-1.2.0
 */
public interface TaskService extends SwiftService {

    /**
     * 主节点分发计划任务
     *
     * @param taskInfo
     * @param target
     */
    void distributeTask(TaskInfo taskInfo, String target);
}
