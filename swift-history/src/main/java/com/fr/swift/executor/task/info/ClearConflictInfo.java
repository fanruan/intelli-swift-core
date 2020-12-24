package com.fr.swift.executor.task.info;

import com.fr.swift.executor.task.type.MigTaskType;
import com.fr.swift.executor.type.ExecutorTaskType;

/**
 * @author Heng.J
 * @date 2020/12/21
 * @description
 * @since swift-1.2.0
 */
public class ClearConflictInfo implements PlanningInfo {

    private String migrateIndex;

    public ClearConflictInfo() {
    }

    public ClearConflictInfo(String migrateIndex) {
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
    public ExecutorTaskType type() {
        return MigTaskType.CLEAR_CONFLICT;
    }
}
