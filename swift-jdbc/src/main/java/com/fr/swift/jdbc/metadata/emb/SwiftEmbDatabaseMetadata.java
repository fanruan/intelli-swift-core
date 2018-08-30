package com.fr.swift.jdbc.metadata.emb;

import com.fr.stable.ArrayUtils;
import com.fr.swift.db.Database;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.jdbc.BaseSwiftDatabaseMetadata;
import com.fr.swift.jdbc.IteratorBasedResultSet;
import com.fr.swift.jdbc.result.ResultSetWrapper;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/30
 */
public class SwiftEmbDatabaseMetadata extends BaseSwiftDatabaseMetadata {
    private Database DB = com.fr.swift.db.impl.SwiftDatabase.getInstance();

    public SwiftEmbDatabaseMetadata(SwiftDatabase schema) {
        super(schema);
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        List<Row> tables = new ArrayList<Row>();
        if (ArrayUtils.contains(types, TABLE)) {
            SwiftDatabase schema = this.schema == null ? SwiftDatabase.valueOf(schemaPattern) : this.schema;
            for (Table table : DB.getAllTables()) {
                SwiftMetaData meta = table.getMeta();
                if (meta.getSwiftDatabase() == schema) {
                    List list = new ArrayList();
                    list.add(meta.getSwiftDatabase().getName());
                    //没有schema,实际上是databaseName
                    list.add(null);
                    list.add(meta.getTableName());
                    list.add("TABLE");
                    tables.add(new ListBasedRow(list));
                }
            }
        }
        SwiftResultSet resultSet = new IteratorBasedResultSet(tables.iterator());
        return new ResultSetWrapper(resultSet);
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        SwiftDatabase schema = this.schema == null ? SwiftDatabase.valueOf(schemaPattern) : this.schema;
        Table table = DB.getTable(new SourceKey(tableNamePattern));
        List<Row> fields = new ArrayList<Row>();
        SwiftMetaData metaData = table.getMeta();
        if (table.getMeta().getSwiftDatabase() == schema) {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                SwiftMetaDataColumn column = metaData.getColumn(i + 1);
                List list = new ArrayList();
                list.add(column.getRemark());
                list.add(column.getName());
                list.add(column.getType());
                list.add(column.getPrecision());
                list.add(column.getScale());
                fields.add(new ListBasedRow(list));
            }
        }
        Map<String, Integer> label2Index = new HashMap<String, Integer>();
        label2Index.put("REMARKS", 1);
        label2Index.put("COLUMN_NAME", 2);
        label2Index.put("DATA_TYPE", 3);
        label2Index.put("COLUMN_SIZE", 4);
        label2Index.put("DECIMAL_DIGITS", 5);
        SwiftResultSet resultSet = new IteratorBasedResultSet(fields.iterator());
        return new ResultSetWrapper(resultSet, label2Index);
    }
}
