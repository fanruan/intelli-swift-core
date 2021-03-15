package com.fr.swift.cloud.executor.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description
 */
public enum SwiftTaskType implements ExecutorTaskType {
    //
    REALTIME,
    RECOVERY,
    TRANSFER,
    INDEX,
    DELETE,
    TRUNCATE,
    COLLATE,
    UPLOAD,
    DOWNLOAD,
    HISTORY,
    QUERY,
    MIGRATE,
    PLANNING; // 通知各个节点建立定时, 用于合并迁移、抽数

    public static List<SwiftTaskType> getAllTypeList() {
        List<SwiftTaskType> typeList = new ArrayList<>();
        Collections.addAll(typeList, SwiftTaskType.values());
        return typeList;
    }

    public static List<SwiftTaskType> getTypeList(SwiftTaskType... types) {
        List<SwiftTaskType> typeList = new ArrayList<>();
        Collections.addAll(typeList, types);
        return typeList;
    }
}