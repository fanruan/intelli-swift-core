package com.fr.swift.source.etl.date;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.DateDerivers;
import com.fr.swift.segment.column.impl.DateType;
import com.fr.swift.segment.column.impl.MixDateType;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.DateUtils;
import com.fr.swift.source.etl.utils.ETLConstant.CONF.ADD_COLUMN.TIME.UNITS;
import com.fr.swift.util.function.Function;

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
        if (segCursor < segments.length && rowCursor < rowCount) {
            rowCount = segments[segCursor].getRowCount();
            Function dateGetter = getDateGetter(type);
            ColumnKey columnKey = new ColumnKey(field);
            checkType(field);
            DictionaryEncodedColumn getter = segments[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            Object value = getter.getValue(getter.getIndexByRow(rowCursor));
            if (value != null){
                value = dateGetter.apply(value);
            }
            Long v = Long.parseLong(value.toString());
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
        } catch (Exception e) {
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

    private static Function getDateGetter(int type) {
        switch (type) {
            case UNITS.YEAR:
                return DateDerivers.newSingleFieldDeriver(DateType.YEAR);
            case UNITS.QUARTER:
                return DateDerivers.newSingleFieldDeriver(DateType.QUARTER);
            case UNITS.MONTH:
                return DateDerivers.newSingleFieldDeriver(DateType.MONTH);
            case UNITS.WEEKDAY:
                return DateDerivers.newSingleFieldDeriver(DateType.WEEK);
            case UNITS.DAY:
                return DateDerivers.newSingleFieldDeriver(DateType.DAY);
            case UNITS.WEEK_COUNT:
                return DateDerivers.newSingleFieldDeriver(DateType.WEEK_OF_YEAR);
            case UNITS.HOUR:
                return DateDerivers.newSingleFieldDeriver(DateType.HOUR);
            case UNITS.MINUTE:
                return DateDerivers.newSingleFieldDeriver(DateType.MINUTE);
            case UNITS.SECOND:
                return DateDerivers.newSingleFieldDeriver(DateType.SECOND);
            case UNITS.YQ:
                return DateDerivers.newTruncatedDeriver(MixDateType.Y_Q);
            case UNITS.YM:
                return DateDerivers.newTruncatedDeriver(MixDateType.Y_M);
            case UNITS.YW:
                return DateDerivers.newTruncatedDeriver(MixDateType.Y_W);
            case UNITS.YMDH:
                return DateDerivers.newTruncatedDeriver(MixDateType.Y_M_D_H);
            case UNITS.YMDHM:
                return DateDerivers.newTruncatedDeriver(MixDateType.Y_M_D_H_M);
            case UNITS.YMDHMS:
                return DateDerivers.newTruncatedDeriver(MixDateType.Y_M_D_H_M_S);
            default:
                return null;
        }
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
