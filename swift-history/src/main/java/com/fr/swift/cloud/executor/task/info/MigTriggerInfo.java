package com.fr.swift.cloud.executor.task.info;

import com.fr.swift.cloud.executor.task.type.MigTaskType;

/**
 * @author Heng.J
 * @date 2020/12/3
 * @description
 * @since swift-1.2.0
 */
public class MigTriggerInfo implements PlanningInfo {

    private String migrateIndex;

    public MigTriggerInfo() {
    }

    public MigTriggerInfo(String migrateIndex) {
        this.migrateIndex = migrateIndex;
    }

    public String getMigrateIndex() {
        return migrateIndex;
    }

    @Override
    public String toString() {
        return "MigrateInfo{" +
                "migrateIndex='" + getMigrateIndex() + '\'' +
                '}';
    }

    @Override
    public MigTaskType type() {
        return MigTaskType.MIGRATE_TRIGGER;
    }
}
