package com.fr.swift.source.db;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.data.core.db.dml.Table;
import com.fr.stable.StringUtils;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/5.
 */
public class TableDBSourceTransfer extends AbstractAllQueryTransfer {
    private String tableName;

    public TableDBSourceTransfer(ConnectionInfo connectionInfo, SwiftMetaData metaData, SwiftMetaData outerMeta, String tableName) {
        super(connectionInfo, metaData, outerMeta);
        this.tableName = tableName;
    }

    @Override
    protected String getQuery(Dialect dialect) throws SQLException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(dialect.column2SQL(metaData.getColumnName(i + 1)));
        }
        String columns = sb.toString();
        Table table = new Table(connectionInfo.getSchema(), tableName);
        return "SELECT " + (StringUtils.isEmpty(columns) ? "*" : columns) + " FROM " + dialect.table2SQL(table);
    }

    @Override
    protected SwiftMetaData getSqlMeta() throws SQLException {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0; i < metaData.getColumnCount(); i++){
            columns.add(outerMeta.getColumn(metaData.getColumnName(i + 1)));
        }
        return new SwiftMetaDataBean(outerMeta.getTableName(), columns);
    }
}
