package com.fr.swift.db;

import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/3/26
 */
public interface Database {
    /**
     * 建表
     *
     * @param meta 元数据
     * @return 表
     * @throws SQLException 异常
     */
    Table createTable(SwiftMetaData meta) throws SQLException;

    /**
     * 拿表
     *
     * @param tableName 表明
     * @return 表
     * @throws SQLException 异常
     */
    Table getTable(String tableName) throws SQLException;

    /**
     * 是否存在表
     *
     * @param tableName 表名
     * @return 是否存在
     */
    boolean existsTable(String tableName);

    /**
     * 改表
     *
     * @param tableName 表名
     * @param meta      新元数据
     * @throws SQLException 异常 是否成功
     */
    void alterTable(String tableName, SwiftMetaData meta) throws SQLException;

    /**
     * 删表
     *
     * @param tableName 表名
     * @throws SQLException 异常 是否成功
     */
    void dropTable(String tableName) throws SQLException;
}