package com.fr.swift.source.db;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/5.
 */
public class QuerySourceTransfer extends AbstractAllQueryTransfer {
    private String query;
    public QuerySourceTransfer(ConnectionInfo connectionInfo, SwiftMetaData metaData, SwiftMetaData outerMeta, String query) {
        super(connectionInfo, metaData, outerMeta);
        this.query = query;
    }

    @Override
    protected String getQuery(Dialect dialect) throws SQLException {
        return query;
    }

    @Override
    protected SwiftMetaData getSqlMeta() {
        return outerMeta;
    }
}
