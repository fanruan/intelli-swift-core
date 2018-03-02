package com.fr.swift.source.etl.detail;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/1/15.
 */
public class DetailOperatorResultSet implements SwiftResultSet {
    private final SwiftMetaData metaData;
    private final List<ColumnKey[]> fields;
    private final Segment[] segments;
    private List<DetailColumn> columns;
    private int currentRow = -1;
    private int currentTotalRow;
    private int currentSegmentIndex = 0;

    public DetailOperatorResultSet(SwiftMetaData metaData, List<ColumnKey[]> fields, Segment[] segments) {
        Util.requireNonNull(fields, segments);
        this.metaData = metaData;
        this.fields = fields;
        this.segments = segments;
        moveToNextSegment();
    }

    private boolean moveToNextSegment() {
        if (currentSegmentIndex >= segments.length) {
            return false;
        }
        columns = new ArrayList<DetailColumn>();
        //pony 暂时先加上原表的数据
        try {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns.add(segments[currentSegmentIndex].getColumn(new ColumnKey(metaData.getColumnName(i))).getDetailColumn());
            }
        } catch (Exception ignore){

        }
        for (ColumnKey[] columnKeys : fields) {
            for (ColumnKey columnKey : columnKeys) {
                columns.add(segments[currentSegmentIndex].getColumn(columnKey).getDetailColumn());
            }
        }
        currentTotalRow = segments[currentSegmentIndex].getRowCount();
        currentRow = -1;
        currentSegmentIndex++;
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        while (++currentRow >= currentTotalRow && moveToNextSegment()) {
        }
        return currentRow < currentTotalRow;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() {
        List list = new ArrayList();
        for (DetailColumn column : columns) {
            list.add(column.get(currentRow));
        }
        return new ListBasedRow(list);
    }
}
