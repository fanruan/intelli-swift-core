package com.fr.swift.db.impl;

import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
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
    private SwiftMetaDataService confSvc = SwiftContext.getInstance().getBean(SwiftMetaDataService.class);

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
        for (Entry<String, SwiftMetaData> entry : confSvc.getAllMetaData().entrySet()) {
            SourceKey tableKey = new SourceKey(entry.getKey());
            tables.add(new SwiftTable(tableKey, entry.getValue()));
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

    public enum Schema {
        /**
         * 默认schema
         */
        CUBE(0, "Cube", "cubes"),
        DECISION_LOG(1, "Decision Log", "log/cubes"),
        BACKUP_CUBE(2, "Backup Cube", "backup_cubes"),
        MINOR_CUBE(3, "Minor Cube", "minor_cubes");

        private final int id;
        private final String name;
        private final String dir;

        Schema(int id, String name, String dir) {
            this.id = id;
            this.name = name;
            this.dir = dir;
        }

        public int getId() {
            return id;
        }

        public String getDir() {
            return dir;
        }

        public String getName() {
            return name;
        }
    }
}