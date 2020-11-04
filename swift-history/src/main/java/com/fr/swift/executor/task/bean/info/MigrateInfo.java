package com.fr.swift.executor.task.bean.info;

import com.fr.swift.executor.task.bean.MigrateBean;

/**
 * @author Heng.J
 * @date 2020/10/29
 * @description
 * @since swift-1.2.0
 */
public class MigrateInfo implements PlanningInfo {

    private String migrateTime;

    private MigrateBean migrateBean;

    public MigrateInfo() {
    }

    public MigrateInfo(String migrateTarget, MigrateBean migrateBean) {
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
}
