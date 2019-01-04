package com.fr.swift.source.resultset;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/6/4
 */
public class IterableResultSet implements SwiftResultSet {

    private SwiftMetaData meta;

    private Iterator<Row> rows;

    public IterableResultSet(Iterable<Row> rows, SwiftMetaData meta) {
        this.meta = meta;
        this.rows = rows.iterator();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return meta;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return rows.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return rows.next();
    }

    @Override
    public void close() throws SQLException {
        rows = null;
    }
}