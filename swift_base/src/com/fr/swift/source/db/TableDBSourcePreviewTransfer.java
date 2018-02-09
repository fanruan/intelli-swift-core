package com.fr.swift.source.db;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dml.Table;
import com.fr.stable.StringUtils;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

import java.util.Map;

/**
 * Created by pony on 2017/12/5.
 */
public class TableDBSourcePreviewTransfer extends AbstractPreviewQueryTransfer {
    private String tableName;

    public TableDBSourcePreviewTransfer(ConnectionInfo connectionInfo, int row, String tableName) {
        super(connectionInfo, row);
        this.tableName = tableName;
    }


    public TableDBSourcePreviewTransfer(ConnectionInfo connectionInfo, Map<String, ColumnType> fieldClassTypes, int row, String tableName) {
        super(connectionInfo, fieldClassTypes, row);
        this.tableName = tableName;
    }

    @Override
    protected String getQuery(Dialect dialect) {
        String columns;
        StringBuffer sb = new StringBuffer();
        if (fieldClassTypes == null || fieldClassTypes.isEmpty()){
            columns = null;
        } else {
            for (String fieldName : fieldClassTypes.keySet()){
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
