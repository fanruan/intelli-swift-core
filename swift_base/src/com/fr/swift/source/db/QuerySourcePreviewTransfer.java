package com.fr.swift.source.db;

import com.fr.data.core.db.dialect.Dialect;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by pony on 2017/12/5.
 */
public class QuerySourcePreviewTransfer extends AbstractPreviewQueryTransfer {
    private String sql;

    public QuerySourcePreviewTransfer(ConnectionInfo connectionInfo, int row, String sql) {
        super(connectionInfo, row);
        this.sql = sql;
    }

    public QuerySourcePreviewTransfer(ConnectionInfo connectionInfo, Map<String, Integer> fieldClassTypes, int row, String sql) {
        super(connectionInfo, fieldClassTypes, row);
        this.sql = sql;
    }

    @Override
    protected String getQuery(Dialect dialect) throws SQLException {
        return sql;
    }
}
