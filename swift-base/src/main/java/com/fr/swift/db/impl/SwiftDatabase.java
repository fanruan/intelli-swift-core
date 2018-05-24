package com.fr.swift.db.impl;

import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftConfigServiceProvider;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class SwiftDatabase implements Database {
    private SwiftConfigService confSvc = SwiftConfigServiceProvider.getInstance();

    @Override
    public synchronized Table createTable(SourceKey tableKey, SwiftMetaData meta) throws SQLException {
        if (existsTable(tableKey)) {
            throw new SQLException("table " + tableKey + " already existed");
        }

        Table table = new SwiftTable(tableKey, meta);
        confSvc.addMetaData(tableKey.getId(), meta);
        return table;
    }

    @Override
    public synchronized Table getTable(SourceKey tableKey) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new SQLException("table " + tableKey + " not exists");
        }
        SwiftMetaData meta = confSvc.getMetaDataByKey(tableKey.getId());
        return new SwiftTable(tableKey, meta);
    }

    @Override
    public synchronized List<Table> getAllTables() {
        List<Table> tables = new ArrayList<Table>();
        try {
            for (Entry<String, SwiftMetaData> entry : confSvc.getAllMetaData().entrySet()) {
                SourceKey tableKey = new SourceKey(entry.getKey());
                SwiftMetaData meta = new SwiftMetaDataImpl(entry.getValue());
                tables.add(new SwiftTable(tableKey, meta));
            }
        } catch (SwiftMetaDataException e) {
            SwiftLoggers.getLogger().error(e);
        }
        return tables;
    }

    @Override
    public synchronized boolean existsTable(SourceKey tableKey) {
        return confSvc.containsMeta(tableKey);
    }

    @Override
    public synchronized void alterTable(SourceKey tableKey, SwiftMetaData meta) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new SQLException("table " + tableKey + " not exists");
        }
        confSvc.updateMetaData(tableKey.getId(), meta);
    }

    @Override
    public synchronized void dropTable(SourceKey tableKey) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new SQLException("table " + tableKey + " not exists");
        }
        confSvc.removeMetaDatas(tableKey.getId());
    }

    private static final Database INSTANCE = new SwiftDatabase();

    private SwiftDatabase() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }
}