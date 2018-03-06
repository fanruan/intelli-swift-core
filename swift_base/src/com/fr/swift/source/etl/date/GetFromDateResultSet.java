package com.fr.swift.source.etl.date;

import com.fr.general.ComparatorUtils;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.utils.DateUtils;
import com.fr.swift.source.etl.utils.ETLConstant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 17:34
 */
public class GetFromDateResultSet implements SwiftResultSet {

    private String field;
    private int type;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private TempValue tempValue;
    private int rowCount, rowCursor, segCursor;

    public GetFromDateResultSet(String field, int type, Segment[] segments, SwiftMetaData metaData) {
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
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if(segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            DateGetter dg = getDateGetter(type);
            ColumnKey columnKey = new ColumnKey(field);
            checkType(field);
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Integer value = dg.get((Long)getter.getValue(getter.getIndexByRow(rowCursor)));
            List list = new ArrayList();
            list.add(value == null ? null : value.longValue());
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

    private void checkType(String field) {
        SwiftMetaDataColumn column = null;
        try {
            column = metaData.getColumn(field);
        } catch(Exception e) {
        }
        DateUtils.checkDateColumnType(column);
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException {
        return tempValue.getRow();
    }

    private static DateGetter getDateGetter(int type){
        if (ComparatorUtils.equals(type, ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.YEAR)) {
            return YearGetter.INSTANCE;
        } else if (ComparatorUtils.equals(type, ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.MONTH)) {
            return MonthGetter.INSTANCE;
        } else if (ComparatorUtils.equals(type, ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.QUARTER)) {
            return SeasonGetter.INSTANCE;
        }
        return SeasonGetter.INSTANCE;
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
