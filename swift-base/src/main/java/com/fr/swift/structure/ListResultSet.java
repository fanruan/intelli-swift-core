package com.fr.swift.structure;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/6/4
 */
public class ListResultSet implements SwiftResultSet {
    private SwiftMetaData meta;

    private List<Row> rows;

    private int cursor = -1;

    public ListResultSet(SwiftMetaData meta, List<Row> rows) {
        this.meta = meta;
        this.rows = rows;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return meta;
    }

    @Override
    public boolean next() throws SQLException {
        return ++cursor < rows.size();
    }

    @Override
    public Row getRowData() throws SQLException {
        return rows.get(cursor);
    }

    @Override
    public void close() throws SQLException {
        rows = null;
    }
}