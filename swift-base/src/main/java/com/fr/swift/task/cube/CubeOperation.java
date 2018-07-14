package com.fr.swift.task.cube;

import com.fr.swift.task.Operation;

/**
 * @author anchore
 * @date 2017/12/8
 */
public enum  CubeOperation implements Operation {
            TRANSPORT_TABLE,
            INDEX_COLUMN,
            MERGE_COLUMN_DICT ,
            INDEX_RELATION,
            INDEX_PATH ,
            INDEX_COLUMN_PATH ,
            NULL ,
            BUILD_TABLE
}