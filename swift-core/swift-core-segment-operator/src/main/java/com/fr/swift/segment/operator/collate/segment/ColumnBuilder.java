package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.collate.segment.bitmap.BitMapShifter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
class ColumnBuilder implements Builder {

    private int rowCount;
    private Column column;
    private List<Segment> subSegments;
    private List<ImmutableBitMap> allShows;
    private List<BitMapShifter> shifters;

    private List<Column> subColumns;
    private List<DetailColumn> subDetailColumns;

    public ColumnBuilder(int rowCount, Column column, String fieldName, List<Segment> subSegments,
                         List<ImmutableBitMap> allShows, List<BitMapShifter> shifters) {
        this.rowCount = rowCount;
        this.column = column;
        this.subSegments = subSegments;
        this.allShows = allShows;
        this.shifters = shifters;
        init(fieldName);
    }

    private void init(String fieldName) {
        ColumnKey key = new ColumnKey(fieldName);
        this.subColumns = new ArrayList<Column>();
        this.subDetailColumns = new ArrayList<DetailColumn>();
        for (int i = 0; i < subSegments.size(); i++) {
            Column c = subSegments.get(i).getColumn(key);
            subColumns.add(c);
            subDetailColumns.add(c.getDetailColumn());
        }
    }

    @Override
    public void build() {
        DetailBuilder detailBuilder = new DetailBuilder(column.getDetailColumn(), subDetailColumns, allShows);
        detailBuilder.build();
        IndexedColumnBuilder indexedColumnBuilder = new IndexedColumnBuilder(rowCount, column.getDictionaryEncodedColumn(),
                column.getBitmapIndex(), subColumns, allShows, shifters);
        indexedColumnBuilder.build();
    }
}

