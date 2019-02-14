package com.fr.swift.source.etl.date;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.DateDerivers;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 17:34
 */
public class GetFromDateResultSet implements SwiftResultSet {

    private String field;
    private GroupType type;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private TempValue tempValue;
    private Function dateGetter;
    private int rowCount, rowCursor, segCursor;

    public GetFromDateResultSet(String field, GroupType type, Segment[] segments, SwiftMetaData metaData) {
        this.field = field;
        this.type = type;
        this.segments = segments;
        this.metaData = metaData;
        init();
    }

    private void init() {
        segCursor = 0;
        rowCursor = 0;
        rowCount = segments[0].getRowCount();
        tempValue = new TempValue();
        dateGetter = DateDerivers.newDeriver(type);
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean hasNext() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            ColumnKey columnKey = new ColumnKey(field);
            checkType(field);
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Object value = getter.getValue(getter.getIndexByRow(rowCursor));
            Long v = value != null ? Long.parseLong(dateGetter.apply(value).toString()) : null;
            List<Object> list = new ArrayList<Object>();
            list.add(v);
            tempValue.setRow(new ListBasedRow(list));
            if (rowCursor < rowCount - 1) {
                rowCursor++;
            } else {
                if (segCursor < segments.length) {
                    segCursor++;
                    rowCursor = 0;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void checkType(String field) {
        SwiftMetaDataColumn column = null;
        try {
            column = metaData.getColumn(field);
        } catch (Exception ignore) {
        }
        ColumnTypeUtils.checkColumnType(column, ColumnTypeConstants.ColumnType.DATE);
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
