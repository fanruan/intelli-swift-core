package com.fr.swift.generate.segment.operator.merger;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/4/24
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
class MergerResultSet implements SwiftResultSet {

    private List<Segment> segmentList;
    private int alloterCount;
    private int currentCount;
    private SwiftMetaData swiftMetaData;
    private List<Row> rowList;
    private int currentSegment;

    public MergerResultSet(List<Segment> segmentList, int alloterCount, SwiftMetaData swiftMetaData) {
        this.segmentList = segmentList;
        this.alloterCount = alloterCount;
        this.swiftMetaData = swiftMetaData;
        this.currentSegment = 0;

        List<String> fieldNames = swiftMetaData.getFieldNames();
        rowList = new ArrayList<Row>();
        for (Segment segment : segmentList) {
            ImmutableBitMap allShowIndex = segment.getAllShowIndex();
            int rowCount = segment.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                //过滤掉不在allshowindex中的项，比如增量删除的项
                if (!allShowIndex.contains(i)) {
                    continue;
                }
                List<Object> values = new ArrayList<Object>();
                for (String fieldName : fieldNames) {
                    Column column = segment.getColumn(new ColumnKey(fieldName));
                    if (column.getBitmapIndex().getNullIndex().contains(i)) {
                        values.add(null);
                    } else {
                        values.add(column.getDetailColumn().get(i));
                    }
                }
                Row row = new ListBasedRow(values);
                rowList.add(row);
            }
        }
    }

    @Override
    public SwiftMetaData getMetaData() {
        return swiftMetaData;
    }

    @Override
    public boolean next() {
        return (currentCount / alloterCount) <= currentSegment && currentCount < rowList.size();
    }

    @Override
    public Row getNextRow() {
        return rowList.get(currentCount++);
    }

    @Override
    public void close() {
        currentSegment++;
    }
}
