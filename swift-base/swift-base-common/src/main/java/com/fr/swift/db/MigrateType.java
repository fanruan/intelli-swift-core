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

    WAITING,
    RUNNING,
    SUCCESS,
    FAILED,
    RETRYING1(1),
    RETRYING2(2),
    RETRYING3(3),
    RETRYING4(4),
    RETRYING5(5);

    private static Map<Integer, MigrateType> retryTimesMap = ImmutableMap.<Integer, MigrateType>builder()
            .put(1, RETRYING1)
            .put(2, RETRYING2)
            .put(3, RETRYING3)
            .put(4, RETRYING4)
            .put(5, RETRYING5)
            .build();

    private int time = 0;

    MigrateType() {
    }

    MigrateType(int time) {
        this.time = time;
    }

    public MigrateType levelUp() {
        return MigrateType.retryTimesMap.getOrDefault(this.time + 1, MigrateType.FAILED);
    }

    public boolean needMigrated() {
        return !MigrateType.SUCCESS.equals(this);
    }

    public boolean isAcceptable() {
        return !MigrateType.WAITING.equals(this);
    }
}
