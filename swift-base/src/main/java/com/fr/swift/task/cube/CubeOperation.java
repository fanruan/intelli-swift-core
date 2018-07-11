package com.fr.swift.task.cube;

import com.fr.swift.task.Operation;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class CubeOperation implements Operation {
    public static final Operation
            TRANSPORT_TABLE = new CubeOperation(),
            INDEX_COLUMN = new CubeOperation(),
            MERGE_COLUMN_DICT = new CubeOperation(),
            INDEX_RELATION = new CubeOperation(),
            INDEX_PATH = new CubeOperation(),
            INDEX_COLUMN_PATH = new CubeOperation(),
            NULL = new CubeOperation(),
            BUILD_TABLE = new CubeOperation();

    private CubeOperation() {
    }
}