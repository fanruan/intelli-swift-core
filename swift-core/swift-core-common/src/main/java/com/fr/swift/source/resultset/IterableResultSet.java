package com.fr.swift.source.resultset;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/6/4
 */
public class IterableResultSet implements SwiftResultSet {

    private SwiftMetaData meta;

    private Iterator<Row> rows;

    private int fetchSize;

    public IterableResultSet(Iterable<Row> rows, SwiftMetaData meta, int fetchSize) {
        this.meta = meta;
        this.rows = rows.iterator();
        this.fetchSize = fetchSize;
    }

    public IterableResultSet(Iterable<Row> rows, SwiftMetaData meta) {
        this(rows, meta, 0);
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public boolean hasNext() {
        return rows.hasNext();
    }

    @Override
    public Row getNextRow() {
        return rows.next();
    }

    @Override
    public void close() {
        rows = null;
    }
}