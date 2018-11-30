package com.fr.swift.api.rpc.impl.rs;

import com.fr.swift.result.serialize.SerializableResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2018/11/30.
 */
public class SerializableResultSetImpl implements SerializableResultSet {

    private int fetchSize;
    protected SwiftMetaData metaData;
    protected List<Row> rows;
    protected int rowCount;
    private transient Iterator<Row> rowIterator;

    public SerializableResultSetImpl(int fetchSize, SwiftMetaData metaData, List<Row> rows, int rowCount) {
        this.fetchSize = fetchSize;
        this.metaData = metaData;
        this.rows = rows == null ? new ArrayList<Row>() : rows;
        this.rowCount = rowCount;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (rowIterator == null) {
            rowIterator = rows.iterator();
        }
        return rowIterator.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        return rowIterator.next();
    }

    @Override
    public void close() throws SQLException {

    }
}
