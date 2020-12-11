package com.fr.swift.db;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author Heng.J
 * @date 2020/10/26
 * @description 迁移状态
 * @since swift-1.2.0
 */
public enum MigrateType {

    BACKUP,
    SUCCESS,
    RUNNING,
    WAITING(0),
    RETRYING1(1),
    RETRYING2(2),
    RETRYING3(3),
    FAILED;

    private static Map<Integer, MigrateType> retryTimesMap = ImmutableMap.<Integer, MigrateType>builder()
            .put(0, WAITING)
            .put(1, RETRYING1)
            .put(2, RETRYING2)
            .put(3, RETRYING3)
            .build();

    private int times = 0;

    MigrateType() {
    }

    MigrateType(int times) {
        this.times = times;
    }

    public MigrateType levelUp() {
        return MigrateType.retryTimesMap.getOrDefault(this.times + 1, MigrateType.FAILED);
    }

    public boolean needMigrated() {
        return retryTimesMap.containsValue(this);
    }

    public int getTimes() {
        return times;
    }
}
