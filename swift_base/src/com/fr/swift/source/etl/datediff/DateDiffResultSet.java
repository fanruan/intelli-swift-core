package com.fr.swift.source.etl.datediff;

import com.fr.general.ComparatorUtils;
import com.fr.swift.exception.meta.SwiftMetaDataException;
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
 * Created by Handsome on 2018/3/2 0002 14:15
 */
public class DateDiffResultSet implements SwiftResultSet {

    private String field1;
    private String field2;
    private int unit;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private int rowCount, rowCursor, segCursor;
    private TempValue tempValue;

    public DateDiffResultSet(String field1, String field2, int unit, Segment[] segments, SwiftMetaData metaData) {
        this.field1 = field1;
        this.field2 = field2;
        this.unit = unit;
        this.segments = segments;
        this.metaData = metaData;
        init();
    }

    private void init() {
        rowCursor = 0;
        segCursor = 0;
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
            DateDiffCalculator dc = getDiffCalculator(unit);
            long systemTime = System.currentTimeMillis();
            ValueGetter g1 = createValueGetter(field1, segments[segCursor], systemTime);
            ValueGetter g2 = createValueGetter(field2, segments[segCursor], systemTime);
            long value = dc.get(g1.getTime(rowCursor), g2.getTime(rowCursor));
            List dataList = new ArrayList();
            dataList.add(value);
            tempValue.setRow(new ListBasedRow(dataList));
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

    private ValueGetter createValueGetter(String field, Segment segment, long systemTime) {
        ValueGetter vg = null;
        if (ComparatorUtils.equals(field, ETLConstant.CONF.FIELD_ID.SYSTEM_TIME)) {
            vg = new SystemTimeValueGetter(systemTime);
        } else {
            vg = new DataValueGetter(segment, new ColumnKey(field));
        }
        vg.check();
        return vg;
    }


    private static DateDiffCalculator getDiffCalculator(int unit) {
        switch (unit) {
            case ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.YEAR:
                return YearDiff.INSTANCE;
            case ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.MONTH:
                return MonthDiff.INSTANCE;
            case ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.QUARTER:
                return SeasonDiff.INSTANCE;
            case ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.DAY:
                return DayDiff.INSTANCE;
            default: {
                return getDiffCalculator(ETLConstant.CONF.ADD_COLUMN.TIME_GAP.UNITS.DAY);
            }
        }
    }

    private class TempValue {
        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }
        private ListBasedRow row;
    }

    private interface ValueGetter {
        Long getTime(int row);

        void check();
    }

    private class SystemTimeValueGetter implements ValueGetter {

        long t;

        SystemTimeValueGetter(long t) {
            this.t = t;
        }

        public Long getTime(int row) {
            return t;
        }

        public void check() {
        }
    }

    private class DataValueGetter implements ValueGetter {
        private Segment segment;
        private DictionaryEncodedColumn getter;
        private ColumnKey columnKey;

        DataValueGetter(Segment segment, ColumnKey columnKey) {
            this.segment = segment;
            this.columnKey = columnKey;
            this.getter = segment.getColumn(columnKey).getDictionaryEncodedColumn();
        }

        public Long getTime(int row) {
            return (Long) getter.getValue(getter.getIndexByRow(row));
        }

        public void check() {
            SwiftMetaDataColumn column = null;
            try {
                column = metaData.getColumn(columnKey.getName());
            } catch(SwiftMetaDataException e) {
            }
            DateUtils.checkDateColumnType(column);
        }
    }
}
