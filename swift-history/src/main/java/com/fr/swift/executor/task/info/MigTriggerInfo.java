package com.fr.swift.executor.task.info;

import com.fr.swift.executor.type.SwiftTaskType;

/**
 * @author Heng.J
 * @date 2020/12/3
 * @description
 * @since swift-1.2.0
 */
public class MigTriggerInfo implements PlanningInfo {

    public MigTriggerInfo() {
    }

    @Override
    public SwiftTaskType type() {
        return SwiftTaskType.MIGRATE_TRIGGER;
    }
}
