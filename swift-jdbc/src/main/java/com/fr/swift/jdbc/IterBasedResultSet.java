package com.fr.swift.jdbc;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Util;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by pony on 2018/8/17.
 */
public class IterBasedResultSet implements SwiftResultSet {
    private Iterator<Row> iterator;

    public IterBasedResultSet(Iterator<Row> iterator) {
        Util.requireNonNull(iterator);
        this.iterator = iterator;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return iterator.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return iterator.next();
    }

    @Override
    public void close() throws SQLException {

    }
}
