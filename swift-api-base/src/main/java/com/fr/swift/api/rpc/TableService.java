package com.fr.swift.api.rpc;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface TableService extends ApiService {
    /**
     * 获取metadata
     *
     * @param schema
     * @param tableName
     * @throws SwiftMetaDataAbsentException
     * @return
     */
    SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException;

    /**
     * 表是否存在
     *
     * @param schema
     * @param tableName
     * @return
     * @throws SwiftMetaDataAbsentException
     */
    boolean isTableExists(SwiftDatabase schema, String tableName);
}
