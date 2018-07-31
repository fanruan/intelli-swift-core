package com.fr.swift.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/26
 */
public class SegmentResultSet implements SwiftResultSet {
    protected Segment seg;

    private List<DetailColumn> details = new ArrayList<DetailColumn>();

    private List<ImmutableBitMap> nullIndices = new ArrayList<ImmutableBitMap>();

    private int cursor = 0;

    private int rowCount = -1;

    public SegmentResultSet(Segment seg) {
        this.seg = seg;
        init();
    }

    private void init() {
        if (!seg.isReadable()) {
            rowCount = 0;
            return;
        }

        SwiftMetaData meta = seg.getMetaData();
        rowCount = seg.getRowCount();

        try {
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                Column<Object> column = seg.getColumn(new ColumnKey(meta.getColumnName(i)));
                details.add(column.getDetailColumn());
                nullIndices.add(column.getBitmapIndex().getNullIndex());
            }
        } catch (Exception ignore) {
        }
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return seg.getMetaData();
    }

    @Override
    public boolean hasNext() {
        return cursor < rowCount;
    }

    @Override
    public Row getNextRow() {
        List<Object> row = new ArrayList<Object>();
        for (int i = 0; i < details.size(); i++) {
            if (nullIndices.get(i).contains(cursor)) {
                row.add(null);
            } else {
                row.add(details.get(i).get(cursor));
            }
        }
        cursor++;
        return new ListBasedRow(row);
    }

    @Override
    public void close() {
        if (seg.isHistory()) {
            for (DetailColumn detail : details) {
                detail.release();
            }
            try {
                SwiftMetaData meta = seg.getMetaData();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    seg.getColumn(new ColumnKey(meta.getColumnName(i))).getBitmapIndex().release();
                }
            } catch (Exception ignore) {
            }
        }
    }
}