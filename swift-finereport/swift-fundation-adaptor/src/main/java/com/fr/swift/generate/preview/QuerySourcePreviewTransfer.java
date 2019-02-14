package com.fr.swift.generate.preview;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.db.AbstractPreviewQueryTransfer;
import com.fr.swift.source.db.ConnectionInfo;

import java.util.Map;

/**
 * @author pony
 * @date 2017/12/5
 */
class QuerySourcePreviewTransfer extends AbstractPreviewQueryTransfer {
    private String sql;

    public QuerySourcePreviewTransfer(ConnectionInfo connectionInfo, int row, String sql) {
        super(connectionInfo, row);
        this.sql = sql;
    }

    public QuerySourcePreviewTransfer(ConnectionInfo connectionInfo, Map<String, ColumnType> fieldClassTypes, int row, String sql) {
        super(connectionInfo, fieldClassTypes, row);
        this.sql = sql;
    }

    @Override
    protected String getQuery(Dialect dialect) {
        return sql;
    }
}