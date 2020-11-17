package com.fr.swift.db;

/**
 * @author Heng.J
 * @date 2020/10/26
 * @description 节点类型
 * @since swift-1.2.0
 */
public enum NodeType {
    TASK,
    MONGO,
    DESIGNER;

    public boolean isMigratable() {
        return this.equals(NodeType.TASK);
    }
}
