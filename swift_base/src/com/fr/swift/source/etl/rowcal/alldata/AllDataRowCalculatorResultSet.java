package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/24 0024 16:11
 */
public class AllDataRowCalculatorResultSet implements SwiftResultSet {

    private String columnName;
    private int columnType;
    private Segment[] segments;
    private RowTraversal[] traversals;
    private SwiftMetaData metaData;
    private double value;
    private AllDataCalculator cal;
    private int segCursor;
    private int rowCursor;
    private int rowCount;
    private TempValue tempValue;

    public AllDataRowCalculatorResultSet(String columnName, int columnType, Segment[] segments,
                                         RowTraversal[] traversals, SwiftMetaData metaData, AllDataCalculator cal) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.segments = segments;
        this.traversals = traversals;
        this.metaData = metaData;
        this.cal = cal;
        init();
    }

    private void init() {
        this.value = cal.get(segments, traversals, new ColumnKey(columnName));
        segCursor = 0;
        rowCursor = 0;
        rowCount = segments[0].getRowCount();
        tempValue = new TempValue();
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if(segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            List list = new ArrayList();
            list.add(value);
            tempValue.setRow(new ListBasedRow(list));
            if(rowCursor < rowCount - 1) {
                rowCursor ++;
            } else {
                if(segCursor < segments.length) {
                    segCursor ++;
                    rowCursor = 0;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException {
        return tempValue.getRow();
    }

    private class TempValue {
        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

        ListBasedRow row;

    }
}
