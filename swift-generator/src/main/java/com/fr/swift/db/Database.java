package com.fr.swift.db;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/3/26
 */
public interface Database {
    /**
     * 建表
     *
     * @param tableKey key
     * @param meta     元数据
     * @return 表
     * @throws SQLException 异常
     */
    Table createTable(SourceKey tableKey, SwiftMetaData meta) throws SQLException;

    /**
     * 删表
     *
     * @param tableKey 表名
     * @throws SQLException 异常 是否成功
     */
    void dropTable(SourceKey tableKey) throws SQLException;

    /**
     * 改表
     *
     * @param tableKey    表key
     * @param alterAction alter操作
     * @throws SQLException 异常 是否成功
     */
    void alterTable(SourceKey tableKey, AlterTableAction alterAction) throws SQLException;

    /**
     * 是否存在表
     *
     * @param tableKey 表key
     * @return 是否存在
     */
    boolean existsTable(SourceKey tableKey);

    /**
     * 拿表
     *
     * @param tableKey 表key
     * @return 表
     */
    Table getTable(SourceKey tableKey);

    List<Table> getAllTables();
}