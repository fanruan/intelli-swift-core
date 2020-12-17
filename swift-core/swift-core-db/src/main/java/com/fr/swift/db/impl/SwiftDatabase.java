package com.fr.swift.db.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.AlreadyExistsException;
import com.fr.swift.db.AlterTableAction;
import com.fr.swift.db.Database;
import com.fr.swift.db.NoSuchTableException;
import com.fr.swift.db.Table;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class SwiftDatabase implements Database, Serializable {
    private final SwiftMetaDataService CONF_SVC = SwiftContext.get().getBean(SwiftMetaDataService.class);

    @Override
    public synchronized Table createTable(SourceKey tableKey, SwiftMetaData meta) throws SQLException {
        if (existsTable(tableKey)) {
            throw new AlreadyExistsException(tableKey.getId());
        }

        Table table = new SwiftTable(tableKey, meta);
        CONF_SVC.saveMeta(meta);
        return table;
    }

    @Override
    public Table differTable(SwiftMetaData meta) throws SQLException {
        Table table = getTable(new SourceKey(meta.getTableName()));
        MetadataDiffer differ = new MetadataDiffer(table.getMeta(), meta);
        if (differ.hasDiff()) {
            List<SwiftMetaDataColumn> dropped = differ.getDropped();
            if (!dropped.isEmpty()) {
                DropColumnAction dropColumnAction = new DropColumnAction(dropped.toArray(new SwiftMetaDataColumn[dropped.size()]));
                table = dropColumnAction.alter(table);
            }
            List<SwiftMetaDataColumn> added = differ.getAdded();
            if (!added.isEmpty()) {
                AddColumnAction addColumnAction = new AddColumnAction(added.toArray(new SwiftMetaDataColumn[added.size()]));
                table = addColumnAction.alter(table);
            }
        }
        return table;
    }

    @Override
    public synchronized Table getTable(SourceKey tableKey) {
        if (!existsTable(tableKey)) {
            return Crasher.crash(new NoSuchTableException(tableKey.getId()));
        }
        SwiftMetaData meta = CONF_SVC.getMeta(tableKey);
        return new SwiftTable(tableKey, meta);
    }

    @Override
    public List<Table> getTablesBySchema(com.fr.swift.db.SwiftDatabase schema) {
        return CONF_SVC.getMetasBySchema(schema).stream()
                .map((m -> new SwiftTable(m.getTableName(), m)))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Table> getAllTables() {
        List<Table> tables = new ArrayList<Table>();
        for (SwiftMetaData metaData : CONF_SVC.getAllMetas()) {
            SourceKey tableKey = new SourceKey(metaData.getTableName());
            tables.add(new SwiftTable(tableKey, metaData));
        }
        return tables;
    }

    @Override
    public synchronized boolean existsTable(SourceKey tableKey) {
        return CONF_SVC.existsMeta(tableKey);
    }

    @Override
    public synchronized void alterTable(SourceKey tableKey, AlterTableAction alterAction) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new NoSuchTableException(tableKey.getId());
        }

        alterAction.alter(getTable(tableKey));
    }

    @Override
    public void updateTable(SwiftMetaData meta) throws SQLException {
        if (!existsTable(new SourceKey(meta.getTableName()))) {
            throw new NoSuchTableException(meta.getTableName());
        }
        CONF_SVC.updateMeta(meta);
    }

    @Override
    public synchronized void dropTable(SourceKey tableKey) throws SQLException {
        if (!existsTable(tableKey)) {
            throw new NoSuchTableException(tableKey.getId());
        }
        CONF_SVC.deleteMeta(tableKey);
    }

    private static final Database INSTANCE = new SwiftDatabase();

    private SwiftDatabase() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

}