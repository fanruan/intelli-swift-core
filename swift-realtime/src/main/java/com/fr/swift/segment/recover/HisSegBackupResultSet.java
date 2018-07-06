package com.fr.swift.segment.recover;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
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
class HisSegBackupResultSet implements SwiftResultSet {
    private SwiftMetaData meta;
    private List<DetailColumn> details = new ArrayList<DetailColumn>();
    private ImmutableBitMap allShowIndex;
    private List<ImmutableBitMap> nullIndices = new ArrayList<ImmutableBitMap>();
    private int cursor = -1;
    private int rowCount;

    HisSegBackupResultSet(Segment seg) throws SQLException {
        init(seg);
    }

    private void init(Segment seg) throws SQLException {
        meta = seg.getMetaData();
        rowCount = seg.getRowCount();
        allShowIndex = seg.getAllShowIndex();

        for (int i = 1; i <= meta.getColumnCount(); i++) {
            Column<Object> column = seg.getColumn(new ColumnKey(meta.getColumnName(i)));
            details.add(column.getDetailColumn());
            nullIndices.add(column.getBitmapIndex().getNullIndex());
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
        for (int i = 0; i < details.size(); i++) {
            if (nullIndices.get(i).contains(cursor)) {
                row.add(null);
            } else {
                row.add(details.get(i).get(cursor));
            }
        }
        return new ListBasedRow(row);
    }

    public ImmutableBitMap getAllShowIndex() {
        return allShowIndex;
    }

    @Override
    public void close() {
//        for (DetailColumn detail : details) {
//            detail.release();
//        }
    }
}