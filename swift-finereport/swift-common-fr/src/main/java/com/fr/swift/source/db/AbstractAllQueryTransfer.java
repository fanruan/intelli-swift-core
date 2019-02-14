package com.fr.swift.source.db;

import com.fr.data.core.db.dialect.Dialect;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.dbdealer.DBDealer;
import com.fr.swift.source.resultset.JdbcResultSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author pony
 * @date 2017/12/5
 */
public abstract class AbstractAllQueryTransfer extends AbstractQueryTransfer {
    protected SwiftMetaData metaData;
    protected SwiftMetaData outerMeta;

    public AbstractAllQueryTransfer(ConnectionInfo connectionInfo, SwiftMetaData metaData, SwiftMetaData outerMeta) {
        super(connectionInfo);
        this.metaData = metaData;
        this.outerMeta = outerMeta;
    }

    /**
     * 获取执行的sql的metadata
     *
     * @return
     * @throws SQLException
     */
    protected abstract SwiftMetaData getSqlMeta() throws SQLException;

    @Override
    public SwiftResultSet createIterator(final ResultSet rs, Dialect dialect, String sql, final Statement stmt, final Connection conn, boolean needCharSetConvert, String originalCharSetName, String newCharSetName) throws SQLException {
        DBDealer[] dealers = createDBDealer(needCharSetConvert, originalCharSetName, newCharSetName, metaData, getSqlMeta());
        return new JdbcResultSet(rs, stmt, conn, metaData, dealers);
    }
}