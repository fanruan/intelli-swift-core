package com.fr.swift.service.event;

import com.fr.swift.event.SwiftEvent;

/**
 * @author Heng.J
 * @date 2020/11/3
 * @description
 * @since swift-1.2.0
 */
public enum NodeEvent implements SwiftEvent {

    // 主节点任务分发启停
    BLOCK,
    ACTIVATE,

    // 主节点迁移重发
    RETRY_DISTRIBUTE,

    // 主节点统筹所有节点准备
    START_WAITING,

    // 主节点对不迁移 但有迁移index节点 冲突任务队列移动执行
    CLEAR_CONFLICT,
}
