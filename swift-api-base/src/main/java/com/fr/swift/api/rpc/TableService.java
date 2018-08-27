package com.fr.swift.api.rpc;

import com.fr.swift.db.Schema;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface TableService {
    /**
     * 获取metadata
     *
     * @param schema
     * @param tableName
     * @return
     */
    SwiftMetaData detectiveMetaData(Schema schema, String tableName) throws SwiftMetaDataAbsentException;

    boolean isTableExists(Schema schema, String tableName) throws SwiftMetaDataAbsentException;
}
