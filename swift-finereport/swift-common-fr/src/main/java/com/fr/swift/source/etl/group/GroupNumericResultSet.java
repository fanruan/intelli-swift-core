package com.fr.swift.source.etl.group;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Handsome on 2018/2/23 0023 09:11
 */
public class GroupNumericResultSet implements SwiftResultSet {

    private ColumnKey columnKey;
    private double max;
    private double min;
    private String useOther;
    private List<RestrictRange> nodes;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private int segCursor, rowCount, rowCursor;
    private TempValue tempValue;

    public GroupNumericResultSet(ColumnKey columnKey, double max, double min, String useOther,
                                 List<RestrictRange> nodes, Segment[] segments, SwiftMetaData metaData) {
        this.columnKey = columnKey;
        this.max = max;
        this.min = min;
        this.useOther = useOther;
        this.nodes = nodes;
        this.segments = segments;
        this.metaData = metaData;
        init();
    }

    private void init() {
        tempValue = new TempValue();
        segCursor = 0;
        rowCount = segments[segCursor].getRowCount();
        rowCursor = 0;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean hasNext() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Object v = getter.getValue(getter.getIndexByRow(rowCursor));
            List dataList = new ArrayList();
            if (null != v) {
                Iterator<RestrictRange> iterator = nodes.iterator();
                while (iterator.hasNext()) {
                    RestrictRange restrictRange = iterator.next();
                    double value = Double.parseDouble(v.toString());
                    if (restrictRange.match(value)) {
                        dataList.add(restrictRange.getGroupName());
                        break;
                    }
                }
                dataList.add(useOther);
            } else {
                dataList.add(useOther);
            }
            tempValue.setRow(new ListBasedRow(dataList));
            if (rowCursor < rowCount - 1) {
                rowCursor++;
            } else {
                if (segCursor < segments.length) {
                    rowCursor = 0;
                    segCursor++;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return tempValue.getRow();
    }

    private class TempValue {
        ListBasedRow row;

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

    }
}
