package com.fr.swift.segment.recover;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
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
    private List<DictionaryEncodedColumn> dicts = new ArrayList<DictionaryEncodedColumn>();

    private int cursor = -1;
    private int rowCount;

    BackupResultSet(Segment seg) throws SQLException {
        init(seg);
    }

    private void init(Segment seg) throws SQLException {
        meta = seg.getMetaData();
        rowCount = seg.getRowCount();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            dicts.add(seg.getColumn(new ColumnKey(meta.getColumnName(i))).getDictionaryEncodedColumn());
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
        for (DictionaryEncodedColumn dict : dicts) {
            row.add(dict.getValueByRow(cursor));
        }
        return new ListBasedRow(row);
    }

    @Override
    public void close() {
        for (DictionaryEncodedColumn dict : dicts) {
            dict.release();
        }
    }
}