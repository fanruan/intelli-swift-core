package com.fr.swift.executor.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description
 */
public enum ExecutorTaskType {
    //swift
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
    //cloud
    TREASURE_UPLOAD;

    public static List<ExecutorTaskType> getAllTypeList() {
        List<ExecutorTaskType> typeList = new ArrayList<ExecutorTaskType>();
        Collections.addAll(typeList, ExecutorTaskType.values());
        return typeList;
    }

    public static List<ExecutorTaskType> getTypeList(ExecutorTaskType... types) {
        List<ExecutorTaskType> typeList = new ArrayList<ExecutorTaskType>();
        Collections.addAll(typeList, types);
        return typeList;
    }
}
