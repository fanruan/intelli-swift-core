package com.fr.swift.cloud.executor.task.bean;

/**
 * @author Heng.J
 * @date 2020/11/2
 * @description
 * @since swift-1.2.0
 */
public class MigrateBean {

    public static final String KEY = "migrateBean";

    private String migrateIndex;

    private String migrateTarget;

    public MigrateBean() {
    }

    public MigrateBean(String migrateIndex, String migrateTarget) {
        this.migrateIndex = migrateIndex;
        this.migrateTarget = migrateTarget;
    }

    public static MigrateBean of(String migrateIndex, String migrateTarget) {
        return new MigrateBean(migrateIndex, migrateTarget);
    }

    public String getMigrateIndex() {
        return migrateIndex;
    }

    public String getMigrateTarget() {
        return migrateTarget;
    }

    @Override
    public String toString() {
        return "MigrateBean{" +
                "migrateIndex='" + getMigrateIndex() + '\'' +
                ", migrateTarget='" + getMigrateTarget() + '\'' +
                '}';
    }
}
