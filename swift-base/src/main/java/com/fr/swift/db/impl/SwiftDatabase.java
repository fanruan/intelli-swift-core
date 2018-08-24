package com.fr.swift.db.impl;

import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.AlreadyExistsException;
import com.fr.swift.db.AlterTableAction;
import com.fr.swift.db.Database;
import com.fr.swift.db.NoSuchTableException;
import com.fr.swift.db.Table;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class SwiftDatabase implements Database {
    private static final SwiftMetaDataService CONF_SVC = SwiftContext.get().getBean(SwiftMetaDataService.class);

    @Override
    public synchronized Table createTable(SourceKey tableKey, SwiftMetaData meta) throws SQLException {
        if (existsTable(tableKey)) {
            throw new AlreadyExistsException(tableKey.getId());
        }

        Table table = new SwiftTable(tableKey, meta);
        CONF_SVC.addMetaData(tableKey.getId(), meta);
        return table;
    }

    @Override
    public synchronized Table getTable(SourceKey tableKey) {
        if (!existsTable(tableKey)) {
            return Crasher.crash(new NoSuchTableException(tableKey.getId()));
        }
        SwiftMetaData meta = CONF_SVC.getMetaDataByKey(tableKey.getId());
        return new SwiftTable(tableKey, meta);
    }

    @Override
    public synchronized List<Table> getAllTables() {
        List<Table> tables = new ArrayList<Table>();
        for (Entry<String, SwiftMetaData> entry : CONF_SVC.getAllMetaData().entrySet()) {
            SourceKey tableKey = new SourceKey(entry.getKey());
            tables.add(new SwiftTable(tableKey, entry.getValue()));
        }
        return tables;
    }

    @Override
    public synchronized boolean existsTable(SourceKey tableKey) {
        return CONF_SVC.containsMeta(tableKey);
    }

    @Override
    public synchronized void alterTable(SourceKey tableKey, AlterTableAction alterAction) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new NoSuchTableException(tableKey.getId());
        }

        alterAction.alter(getTable(tableKey));
    }

    @Override
    public synchronized void dropTable(SourceKey tableKey) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new NoSuchTableException(tableKey.getId());
        }
        CONF_SVC.removeMetaDatas(tableKey.getId());
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
        CUBE(0, "cube", "cubes"),
        DECISION_LOG(1, "decision_log", "logs/cubes"),
        MINOR_CUBE(2, "minor_cube", "minor_cubes");

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

        public String getBackupDir() {
            return String.format("%s/bak", dir);
        }

        public String getName() {
            return name;
        }
    }
}