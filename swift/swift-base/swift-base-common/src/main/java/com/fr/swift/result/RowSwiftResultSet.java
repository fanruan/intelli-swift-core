package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2018/12/28.
 */
public class RowSwiftResultSet implements SwiftResultSet, Serializable {

    private static final long serialVersionUID = -6947348134779980076L;
    private SwiftMetaData metaData;
    private List<Row> rows;
    private transient Iterator<Row> iterator;

    public RowSwiftResultSet(SwiftMetaData metaData, List<Row> rows) {
        this.metaData = metaData;
        this.rows = rows == null ? new ArrayList<Row>() : rows;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public boolean hasNext() throws SQLException {
        if (iterator == null) {
            iterator = rows.iterator();
        }
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
