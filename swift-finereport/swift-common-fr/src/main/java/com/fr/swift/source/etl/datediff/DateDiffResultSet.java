package com.fr.swift.source.etl.datediff;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 14:15
 */
public class DateDiffResultSet implements SwiftResultSet {

    private String field1;
    private String field2;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private int rowCount, rowCursor, segCursor;
    private TempValue tempValue;
    private DateDiffCalculator calculator;

    public DateDiffResultSet(String field1, String field2, GroupType groupType, Segment[] segments, SwiftMetaData metaData) {
        this.field1 = field1;
        this.field2 = field2;
        this.segments = segments;
        this.metaData = metaData;
        init(groupType);
    }

    private static DateDiffCalculator getDiffCalculator(GroupType groupType) {
        switch (groupType) {
            case YEAR:
                return new YearDiff();
            case MONTH:
                return new MonthDiff();
            case QUARTER:
                return new SeasonDiff();
            case WEEK:
                return new WeekDiffer();
            case DAY:
                return DayDiff.INSTANCE;
            case HOUR:
                return HourDiffer.INSTANCE;
            case MINUTE:
                return MinuteDiffer.INSTANCE;
            case SECOND:
                return SecondDiffer.INSTANCE;
            default:
                return null;
        }
    }

    private void init(GroupType groupType) {
        rowCursor = 0;
        segCursor = 0;
        rowCount = segments[0].getRowCount();
        tempValue = new TempValue();
        calculator = getDiffCalculator(groupType);
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean hasNext() throws SQLException {
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            long systemTime = System.currentTimeMillis();
            ValueGetter g1 = createValueGetter(field1, segments[segCursor], systemTime);
            ValueGetter g2 = createValueGetter(field2, segments[segCursor], systemTime);
            Object value = calculator.get(g1.getTime(rowCursor), g2.getTime(rowCursor));
            List dataList = new ArrayList();
            dataList.add(value == null ? value : Long.parseLong(value.toString()));
            tempValue.setRow(new ListBasedRow(dataList));
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

    private ValueGetter createValueGetter(String field, Segment segment, long systemTime) {
        ValueGetter vg;
        Column column = segment.getColumn(new ColumnKey(field));
        if (column == null) {
            vg = new SystemTimeValueGetter(systemTime);
        } else {
            SwiftMetaDataColumn metaDataColumn = null;
            try {
                metaDataColumn = segment.getMetaData().getColumn(field);
            } catch (SwiftMetaDataException ignore) {
            }
            ColumnTypeUtils.checkColumnType(metaDataColumn, ColumnTypeConstants.ColumnType.DATE);
            vg = new DataValueGetter(column.getDictionaryEncodedColumn());
        }
        return vg;
    }

    private interface ValueGetter {
        Long getTime(int row);
    }

    private class TempValue {
        private ListBasedRow row;

        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }
    }

    private class SystemTimeValueGetter implements ValueGetter {

        Long t;

        SystemTimeValueGetter(long t) {
            this.t = t;
        }

        @Override
        public Long getTime(int row) {
            return t;
        }
    }

    private class DataValueGetter implements ValueGetter {
        private DictionaryEncodedColumn dic;

        DataValueGetter(DictionaryEncodedColumn dic) {
            this.dic = dic;
        }

        @Override
        public Long getTime(int row) {
            return (Long) dic.getValue(dic.getIndexByRow(row));
        }
    }
}
