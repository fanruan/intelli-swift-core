package com.fr.swift.segment.recover;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/6/4
 */
class BackupResultSet implements SwiftResultSet {
    private SwiftMetaData meta;
    private List<DetailColumn> details = new ArrayList<DetailColumn>();
    private int cursor = -1;
    private int rowCount;

    BackupResultSet(Segment seg) throws SQLException {
        init(seg);
    }

    private void init(Segment seg) throws SQLException {
        meta = seg.getMetaData();
        rowCount = seg.getRowCount();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            details.add(seg.getColumn(new ColumnKey(meta.getColumnName(i))).getDetailColumn());
        }
    }

    @Override
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public boolean next() {
        return ++cursor < rowCount;
    }

    @Override
    public Row getRowData() {
        List<Object> row = new ArrayList<Object>();
        for (DetailColumn detail : details) {
            row.add(detail.get(cursor));
        }
        return new ListBasedRow(row);
    }

    @Override
    public void close() {
        for (DetailColumn detail : details) {
            detail.release();
        }
    }
}