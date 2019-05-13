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
public enum SwiftTaskType implements ExecutorTaskType {
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
    QUERY;


    public static List<SwiftTaskType> getAllTypeList() {
        List<SwiftTaskType> typeList = new ArrayList<SwiftTaskType>();
        Collections.addAll(typeList, SwiftTaskType.values());
        return typeList;
    }

    public static List<SwiftTaskType> getTypeList(SwiftTaskType... types) {
        List<SwiftTaskType> typeList = new ArrayList<SwiftTaskType>();
        Collections.addAll(typeList, types);
        return typeList;
    }
}