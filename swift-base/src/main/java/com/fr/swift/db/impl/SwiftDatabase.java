package com.fr.swift.db.impl;

import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftConfigServiceProvider;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;
import com.fr.third.javax.persistence.AttributeConverter;

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
        SwiftMetaData meta = SwiftConfigServiceProvider.getInstance().getMetaDataByKey(tableKey.getId());
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
        CUBE(0, "cube", "cubes"),
        DECISION_LOG(1, "decision_log", "log/cubes");

        public final int id;
        public final String name;
        public final String dir;

        Schema(int id, String name, String dir) {
            this.id = id;
            this.name = name;
            this.dir = dir;
        }

        public static class SchemaConverter implements AttributeConverter<Schema, Integer> {
            @Override
            public Integer convertToDatabaseColumn(Schema schema) {
                return schema.id;
            }

            @Override
            public Schema convertToEntityAttribute(Integer integer) {
                for (Schema schema : Schema.values()) {
                    if (schema.id == integer) {
                        return schema;
                    }
                }
                return Crasher.crash("no type fits: " + integer);
            }
        }
    }
}