package com.fr.swift.cloud.executor.task.info;

import com.fr.swift.cloud.executor.task.bean.MigrateBean;
import com.fr.swift.cloud.executor.task.type.MigTaskType;

/**
 * @author Heng.J
 * @date 2020/10/29
 * @description
 * @since swift-1.2.0
 */
public class MigScheduleInfo implements PlanningInfo {

    private String migrateTime;

    private MigrateBean migrateBean;

    public MigScheduleInfo() {
    }

    public MigScheduleInfo(String migrateTarget, MigrateBean migrateBean) {
        this.migrateTime = migrateTarget;
        this.migrateBean = migrateBean;
    }

    public String getMigrateTime() {
        return migrateTime;
    }

    public MigrateBean getMigrateBean() {
        return migrateBean;
    }

    @Override
    public String toString() {
        return "MigrateInfo{" +
                "migrateTime='" + getMigrateTime() + '\'' +
                ", migrateBean='" + getMigrateBean() + '\'' +
                '}';
    }

    @Override
    public MigTaskType type() {
        return MigTaskType.MIGRATE_SCHEDULE;
    }
}
