package com.fr.swift.cube.task.impl;

/**
 * @author anchore
 * @date 2017/12/8
 */
public enum Operation {
    // 操作
    TRANSPORT_TABLE,
    INDEX_COLUMN, INDEX_RELATION, INDEX_PATH, INDEX_COLUMN_PATH,
    //全局字典
    MERGER_COLUMN,

    // 取数然后索引
    BUILD_TABLE,

    // 咸鱼任务
    NULL
}