package com.fr.swift.executor.type;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description
 */
public enum ExecutorTaskType {
    REALTIME,
    TRANSFER,
    INDEX,
    DELETE,
    COLLATE,
    UPLOAD,
    DOWNLOAD,
    HISTORY,
    QUERY;

    public static List<ExecutorTaskType> getAllTypeList() {
        List<ExecutorTaskType> typeList = new ArrayList<ExecutorTaskType>();
        for (ExecutorTaskType type : ExecutorTaskType.values()) {
            typeList.add(type);
        }
        return typeList;
    }

    public static List<ExecutorTaskType> getTypeList(ExecutorTaskType... types) {
        List<ExecutorTaskType> typeList = new ArrayList<ExecutorTaskType>();
        for (ExecutorTaskType type : types) {
            typeList.add(type);
        }
        return typeList;
    }
}
