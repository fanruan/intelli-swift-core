package com.fr.swift.db.impl;

import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class SwiftDatabase implements Database {
    private Map<String, Table> tables = new ConcurrentHashMap<String, Table>();

    @Override
    public Table createTable(SwiftMetaData meta) throws SQLException {
        if (existsTable(meta.getTableName())) {
            throw new SQLException("table already existed");
        }

        Table table = new SwiftTable(meta);
        tables.put(meta.getTableName(), table);
        return table;
    }

    @Override
    public Table getTable(String tableName) throws SQLException {
        if (!existsTable(tableName)) {
            throw new SQLException("table not exists");
        }

        return tables.get(tableName);
    }

    @Override
    public boolean existsTable(String tableName) {
        return tables.containsKey(tableName);
    }


    @Override
    public void alterTable(String tableName, SwiftMetaData meta) throws SQLException {
        if (!existsTable(tableName)) {
            throw new SQLException("table not exists");
        }
        tables.get(tableName).setMeta(meta);
    }

    @Override
    public void dropTable(String tableName) throws SQLException {
        if (!existsTable(tableName)) {
            throw new SQLException("table not exists");
        }
        tables.remove(tableName);
    }
}