package com.fr.swift.api.rpc;

import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

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
     * @return
     * @throws SwiftMetaDataAbsentException
     */
    SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException;

    /**
     * 获取数据库下的所有表名
     *
     * @param schema
     * @return
     */
    List<SwiftMetaData> detectiveAllTable(SwiftDatabase schema);

    /**
     * 表是否存在
     *
     * @param schema
     * @param tableName
     * @return
     * @throws SwiftMetaDataAbsentException
     */
    boolean isTableExists(SwiftDatabase schema, String tableName);

    /**
     * 建表接口
     *
     * @param schema
     * @param tableName
     * @param columns
     * @return
     * @throws Exception
     */
    int createTable(SwiftDatabase schema, String tableName, List<Column> columns) throws Exception;

    /**
     * 删除表接口
     *
     * @param schema
     * @param tableName
     * @throws Exception
     */
    int dropTable(SwiftDatabase schema, String tableName) throws Exception;

    /**
     * 清空数据
     *
     * @param schema
     * @param tableName
     * @throws Exception
     */
    void truncateTable(SwiftDatabase schema, String tableName) throws Exception;

    /**
     * 加字段
     *
     * @param column
     * @return
     */
    boolean addColumn(SwiftDatabase schema, String tableName, Column column) throws Exception;

    /**
     * 删字段
     *
     * @param columnName
     * @return
     */
    boolean dropColumn(SwiftDatabase schema, String tableName, String columnName) throws Exception;
}
