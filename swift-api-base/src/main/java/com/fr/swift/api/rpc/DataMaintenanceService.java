package com.fr.swift.api.rpc;

import com.fr.swift.db.Where;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface DataMaintenanceService {
    /**
     * 插入指定字段数据
     *
     * @param tableName
     * @param fields
     * @param rows
     * @return
     * @throws SQLException
     */
    int insert(String tableName, List<String> fields, List<Row> rows) throws SQLException;

    /**
     * 插入所有字段
     *
     * @param tableName
     * @param rows
     * @return
     * @throws SQLException
     */
    int insert(String tableName, List<Row> rows) throws SQLException;

    /**
     * 使用resultSet插入
     *
     * @param tableName
     * @param resultSet
     * @return
     * @throws SQLException
     */
    int insert(String tableName, SwiftResultSet resultSet) throws SQLException;

    /**
     * TODO 先占个坑具体再调
     *
     * @param tableName
     * @param where
     * @return
     */
    int delete(String tableName, Where where);

    /**
     * TODO 先占个坑具体再调
     *
     * @param tableName
     * @param resultSet
     * @param where
     * @return
     */
    int update(String tableName, SwiftResultSet resultSet, Where where);
}
