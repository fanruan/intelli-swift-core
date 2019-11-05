package com.fr.swift.db;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/8/14
 */
public interface AlterTableAction {
    /**
     * alter table
     *
     * @param table 表
     * @throws SQLException 异常
     */
    void alter(Table table) throws SQLException;
}