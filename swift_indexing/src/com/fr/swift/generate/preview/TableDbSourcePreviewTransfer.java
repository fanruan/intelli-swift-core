package com.fr.swift.generate.preview;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dml.Table;
import com.fr.stable.StringUtils;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.db.AbstractPreviewQueryTransfer;
import com.fr.swift.source.db.ConnectionInfo;

import java.util.Map;

/**
 * @author pony
 * @date 2017/12/5
 */
class TableDbSourcePreviewTransfer extends AbstractPreviewQueryTransfer {
    private String tableName;

    public TableDbSourcePreviewTransfer(ConnectionInfo connectionInfo, int row, String tableName) {
        super(connectionInfo, row);
        this.tableName = tableName;
    }


    public TableDbSourcePreviewTransfer(ConnectionInfo connectionInfo, Map<String, ColumnType> fieldClassTypes, int row, String tableName) {
        super(connectionInfo, fieldClassTypes, row);
        this.tableName = tableName;
    }

    @Override
    protected String getQuery(Dialect dialect) {
        String columns;
        StringBuilder sb = new StringBuilder();
        if (fieldClassTypes == null || fieldClassTypes.isEmpty()) {
            columns = null;
        } else {
            for (String fieldName : fieldClassTypes.keySet()) {
                sb.append(dialect.column2SQL(fieldName));
                sb.append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            columns = sb.toString();
        }
        Table table = new Table(connectionInfo.getSchema(), tableName);
        return "SELECT " + (StringUtils.isEmpty(columns) ? "*" : columns) + " FROM " + dialect.table2SQL(table);
    }

}
