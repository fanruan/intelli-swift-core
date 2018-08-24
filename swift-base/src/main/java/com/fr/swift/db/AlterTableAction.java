package com.fr.swift.db;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/8/14
 */
public interface AlterTableAction {
    void alter(Table table) throws SQLException;
}