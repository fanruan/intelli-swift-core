package com.fr.swift.generate.preview;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dml.Table;
import com.fr.stable.StringUtils;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.AbstractPreviewQueryTransfer;
import com.fr.swift.source.db.ConnectionInfo;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author pony
 * @date 2017/12/5
 */
class TableDbSourcePreviewTransfer extends AbstractPreviewQueryTransfer {
    private String tableName;
    private SwiftMetaData metaData;

    public TableDbSourcePreviewTransfer(ConnectionInfo connectionInfo, int row, String tableName) {
        super(connectionInfo, row);
        this.tableName = tableName;
    }


    public TableDbSourcePreviewTransfer(ConnectionInfo connectionInfo, Map<String, ColumnType> fieldClassTypes, SwiftMetaData metadata, int row, String tableName) {
        super(connectionInfo, fieldClassTypes, row);
        this.tableName = tableName;
        this.metaData = metadata;
    }

    @Override
    protected String getQuery(Dialect dialect) throws SQLException {
        String columns;
        StringBuilder sb = new StringBuilder();
        if (metaData == null) {
            columns = null;
        } else {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                sb.append(dialect.column2SQL(metaData.getColumnName(i + 1)));
                sb.append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            columns = sb.toString();
        }
        Table table = new Table(connectionInfo.getSchema(), tableName);
        return "SELECT " + (StringUtils.isEmpty(columns) ? "*" : columns) + " FROM " + dialect.table2SQL(table);
    }

}
