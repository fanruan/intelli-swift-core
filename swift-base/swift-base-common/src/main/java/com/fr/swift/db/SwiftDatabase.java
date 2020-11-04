package com.fr.swift.db;

/**
 * @author yee
 * @date 2018/8/27
 */
public enum SwiftDatabase {
    /**
     * 默认schema
     */
    //H.J TODO : 2020/10/28 更改已有的表配置从 cube 为 cloud 同时迁移也只针对 cloud
    CUBE(0, "cube", "cubes"),
    DECISION_LOG(1, "decision_log", "logs/cubes"),
    MINOR_CUBE(2, "minor_cube", "minor_cubes");

    private final int id;
    private final String name;
    private final String dir;

    SwiftDatabase(int id, String name, String dir) {
        this.id = id;
        this.name = name;
        this.dir = dir;
    }

    public int getId() {
        return id;
    }

    public String getDir() {
        return dir;
    }

    public String getBackupDir() {
        return String.format("%s/bak", dir);
    }

    public String getName() {
        return name;
    }

    public static SwiftDatabase fromKey(String key) {
        return valueOf(key.toUpperCase());
    }
}