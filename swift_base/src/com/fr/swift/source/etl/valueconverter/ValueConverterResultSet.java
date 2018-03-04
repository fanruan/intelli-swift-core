package com.fr.swift.source.etl.valueconverter;

import com.fr.general.GeneralUtils;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.utils.DateUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Handsome on 2018/2/23 0023 11:56
 */
public class ValueConverterResultSet implements SwiftResultSet {

    private String column;
    private int columnType;
    private Segment[] segment;
    private SwiftMetaData metaData;
    private int segCursor;
    private int rowCursor;
    private int rowCount;
    private ColumnKey columnKey;
    private TempValue tempValue;

    public ValueConverterResultSet(String column, int columnType, Segment[] segment, SwiftMetaData metaData) {
        this.column = column;
        this.columnType = columnType;
        this.segment = segment;
        this.metaData = metaData;
        init();
    }

    public void init() {
        segCursor = 0;
        rowCursor = 0;
        rowCount = segment[segCursor].getRowCount();
        columnKey = new ColumnKey(column);
        tempValue = new TempValue();
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if(segCursor < segment.length && rowCursor < rowCount) {
            rowCount = segment[segCursor].getRowCount();
            DictionaryEncodedColumn getter = segment[segCursor].getColumn(columnKey).getDictionaryEncodedColumn();
            int fieldType = metaData.getColumn(columnKey.getName()).getType();
            Object value = checkValueType(getter.getValue(getter.getIndexByRow(rowCursor)), fieldType);
            List list = new ArrayList();
            list.add(value);
            tempValue.setRow(new ListBasedRow(list));
            if(rowCursor < rowCount - 1) {
                rowCursor ++;
            } else {
                if(segCursor < segment.length) {
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

    private Object checkValueType(Object value, int fieldType) {
        if(value == null) {
            return null;
        }
        switch(ColumnTypeUtils.sqlTypeToColumnType(fieldType, 1, 1)) {
            case NUMBER: {
                return convertNumber((Number) value);
            }
            case DATE: {
                return convertDate((Long) value);
            }
            default: {
                return convertString(value.toString());
            }
        }
    }

    private Object convertString(String value) {
        switch(ColumnTypeConstants.ColumnType.values()[columnType]) {
            case NUMBER: {
                try {
                    return Double.parseDouble(value);
                } catch (Exception e) {
                }
                break;
            }
            case DATE: {
                try {
                    Date d = DateUtils.parse(value);
                    return d.getTime();
                } catch (Exception e) {
                }
                break;
            }
        }
        return null;
    }

    private Object convertDate(Long value) {
        switch(ColumnTypeConstants.ColumnType.values()[columnType]) {
            case DATE: {
                return value;
            }
            case STRING: {
                return DateUtils.format(new Date(value));
            }
        }
        return null;
    }

    private Object convertNumber(Number value) {
        switch(ColumnTypeConstants.ColumnType.values()[columnType]) {
            case STRING: {
                return GeneralUtils.objectToString(value);
            }
            case DATE: {
                return value.longValue();
            }
        }
        return null;
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
