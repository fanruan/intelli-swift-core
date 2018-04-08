package com.fr.swift.db.impl;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class SwiftDatabase implements Database {
    @Override
    public Table createTable(SourceKey tableKey, SwiftMetaData meta) throws SQLException {
        if (existsTable(tableKey)) {
            throw new SQLException("table already existed");
        }

        Table table = new SwiftTable(tableKey, meta);
        MetaDataConfig.getInstance().addMetaData(tableKey.getId(), MetaDataConvertUtil.convert2ConfigMetaData(meta));
        return table;
    }

    @Override
    public Table getTable(SourceKey tableKey) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new SQLException("table not exists");
        }
        SwiftMetaData meta = MetaDataConvertUtil.getSwiftMetaDataBySourceKey(tableKey.getId());
        return new SwiftTable(tableKey, meta);
    }

    @Override
    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<Table>();
        for (Entry<String, IMetaData> entry : MetaDataConfig.getInstance().getAllMetaData().entrySet()) {
            SourceKey tableKey = new SourceKey(entry.getKey());
            SwiftMetaData meta = MetaDataConvertUtil.toSwiftMetadata(entry.getValue());
            tables.add(new SwiftTable(tableKey, meta));
        }
        return tables;
    }

    @Override
    public synchronized boolean existsTable(SourceKey tableKey) {
        return MetaDataConfig.getInstance().contains(tableKey);
    }

    @Override
    public void alterTable(SourceKey tableKey, SwiftMetaData meta) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new SQLException("table not exists");
        }
        MetaDataConfig.getInstance().modifyMetaData(tableKey.getId(), MetaDataConvertUtil.convert2ConfigMetaData(meta));
    }

    @Override
    public void dropTable(SourceKey tableKey) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new SQLException("table not exists");
        }
        MetaDataConfig.getInstance().removeMetaData(tableKey.getId());
    }

    private static final Database INSTANCE = new SwiftDatabase();

    public static Database getInstance() {
        return INSTANCE;
    }
}