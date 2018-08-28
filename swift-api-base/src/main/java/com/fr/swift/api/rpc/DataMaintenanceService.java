package com.fr.swift.api.rpc;

import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface DataMaintenanceService extends ApiService {
    /**
     * 插入指定字段数据
     *
     * @param schema
     * @param tableName
     * @param fields
     * @param rows
     * @return
     * @throws SQLException
     */
    int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws SQLException;

    /**
     * 插入所有字段
     *
     * @param schema
     * @param tableName
     * @param rows
     * @return
     * @throws SQLException
     */
    int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws SQLException;

    /**
     * 将某查询的结果插入表中
     *
     * @param schema
     * @param tableName
     * @param queryJson
     * @return
     * @throws SQLException
     */
    int insert(SwiftDatabase schema, String tableName, String queryJson) throws SQLException;

    /**
     * 删除接口
     *
     * @param schema
     * @param tableName
     * @param where
     * @return
     */
    int delete(SwiftDatabase schema, String tableName, Where where);

    /**
     * 修改数据接口
     *
     * @param schema
     * @param tableName
     * @param resultSet
     * @param where
     * @return
     */
    int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where);

    /**
     * 建表接口
     *
     * @param schema
     * @param tableName
     * @param columns
     * @return
     */
    int createTable(SwiftDatabase schema, String tableName, List<Column> columns);
}
