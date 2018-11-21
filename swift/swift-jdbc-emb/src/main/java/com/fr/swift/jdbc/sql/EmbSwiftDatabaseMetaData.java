package com.fr.swift.jdbc.sql;

import com.fr.stable.ArrayUtils;
import com.fr.swift.db.Database;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.jdbc.IteratorBasedResultSet;
import com.fr.swift.jdbc.SwiftJdbcConstants;
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
 * @date 2018/11/20
 */
public class EmbSwiftDatabaseMetaData extends SwiftDataBaseMetaData {
    protected SwiftDatabase schema;
    private Database db;

    public EmbSwiftDatabaseMetaData(BaseSwiftConnection connection) {
        super(connection);
        this.schema = SwiftDatabase.fromKey(connection.getConfig().swiftDatabase());
        this.db = com.fr.swift.db.impl.SwiftDatabase.getInstance();
    }

    protected void dealColumns(List<Row> fields, SwiftMetaData metaData) throws SwiftMetaDataException {
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

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        final List<Row> tables = new ArrayList<Row>();
        if (ArrayUtils.contains(types, SwiftJdbcConstants.TABLE)) {
            final SwiftDatabase schema = (this.schema == null) ? SwiftDatabase.valueOf(schemaPattern) : this.schema;
            for (final Table table : this.db.getAllTables()) {
                final SwiftMetaData meta = table.getMeta();
                if (meta.getSwiftDatabase() == schema) {
                    final List list = new ArrayList();
                    list.add(meta.getSwiftDatabase().getName());
                    list.add(null);
                    list.add(meta.getTableName());
                    list.add(SwiftJdbcConstants.TABLE);
                    tables.add(new ListBasedRow(list));
                }
            }
        }
        final SwiftResultSet resultSet = new IteratorBasedResultSet(tables.iterator());
        return new ResultSetWrapper(resultSet);
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        final SwiftDatabase schema = (this.schema == null) ? SwiftDatabase.valueOf(schemaPattern) : this.schema;
        final Table table = this.db.getTable(new SourceKey(tableNamePattern));
        final List<Row> fields = new ArrayList<Row>();
        final SwiftMetaData metaData = table.getMeta();
        if (table.getMeta().getSwiftDatabase() == schema) {
            this.dealColumns(fields, metaData);
        }
        final Map<String, Integer> label2Index = new HashMap<String, Integer>();
        label2Index.put("REMARKS", 1);
        label2Index.put("COLUMN_NAME", 2);
        label2Index.put("DATA_TYPE", 3);
        label2Index.put("COLUMN_SIZE", 4);
        label2Index.put("DECIMAL_DIGITS", 5);
        final SwiftResultSet resultSet = new IteratorBasedResultSet(fields.iterator());
        return new ResultSetWrapper(resultSet, label2Index);
    }
}
