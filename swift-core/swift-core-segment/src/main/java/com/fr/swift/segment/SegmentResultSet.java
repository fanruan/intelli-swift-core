package com.fr.swift.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.EmptyBitmap;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/26
 */
public class SegmentResultSet implements SwiftResultSet {
    private Segment seg;

    private List<DetailColumn> details = new ArrayList<DetailColumn>();

    private List<ImmutableBitMap> nullIndices = new ArrayList<ImmutableBitMap>();

    protected ImmutableBitMap allShowIndex;

    private boolean discardDeleted;

    private int cursor = 0;

    private int rowCount = -1;

    public SegmentResultSet(Segment seg) {
        this(seg, true);
    }

    public SegmentResultSet(Segment seg, boolean discardDeleted) {
        this.seg = seg;
        this.discardDeleted = discardDeleted;
        init();
    }

    private void init() {
        if (!seg.isReadable()) {
            rowCount = 0;
            allShowIndex = AllShowBitMap.of(rowCount);
            return;
        }

        SwiftMetaData meta = seg.getMetaData();
        rowCount = seg.getRowCount();
        try {
            allShowIndex = seg.getAllShowIndex();
        } catch (Exception e) {
            allShowIndex = AllShowBitMap.of(rowCount);
        }

        try {
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                Column<Object> column = seg.getColumn(new ColumnKey(meta.getColumnName(i)));
                details.add(column.getDetailColumn());
                ImmutableBitMap nullIndex;
                try {
                    nullIndex = column.getBitmapIndex().getNullIndex();
                } catch (Exception e) {
                    nullIndex = new EmptyBitmap();
                }
                nullIndices.add(nullIndex);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
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
        if (discardDeleted) {
            while (cursor < rowCount && !allShowIndex.contains(cursor)) {
                cursor++;
            }
        }
        return cursor < rowCount;
    }

    @Override
    public Row getNextRow() {
        List<Object> row = new ArrayList<Object>();
        for (int i = 0; i < details.size(); i++) {
            if (nullIndices.get(i).contains(cursor)) {
                row.add(null);
            } else {
                row.add(getDetail(details.get(i), cursor));
            }
        }
        cursor++;
        return new ListBasedRow(row);
    }

    protected <T> T getDetail(DetailColumn<T> detail, int cursor) {
        return detail.get(cursor);
    }

    @Override
    public void close() {
        SegmentUtils.release(seg);
        SegmentUtils.releaseColumnsOf(seg);
    }
}