package com.fr.swift.task.cube;

import com.fr.swift.task.Operation;

/**
 * @author anchore
 * @date 2017/12/8
 */
public enum CubeOperation implements Operation {
    // 取数
    TRANSPORT_TABLE,

    // 索引
    INDEX_COLUMN,
    MERGE_COLUMN_DICT,
    INDEX_RELATION,
    INDEX_PATH,
    INDEX_COLUMN_PATH,

    // 咸鱼操作
    NULL,
    BUILD_TABLE
}