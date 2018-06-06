package com.fr.swift.segment;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LimitedResultSet implements SwiftResultSet {
    private SwiftResultSet origin;

    private int cursor = -1;

    private int limit;

    public LimitedResultSet(SwiftResultSet origin, int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException();
        }
        this.origin = origin;
        this.limit = limit;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return origin.getMetaData();
    }

    @Override
    public boolean next() throws SQLException {
        return ++cursor < limit && origin.next();
    }

    @Override
    public Row getRowData() throws SQLException {
        return origin.getRowData();
    }

    @Override
    public void close() {
    }
}