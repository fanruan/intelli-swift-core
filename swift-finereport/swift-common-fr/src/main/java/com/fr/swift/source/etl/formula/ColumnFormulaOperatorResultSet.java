package com.fr.swift.source.etl.formula;

import com.fr.base.Utils;
import com.fr.general.DateUtils;
import com.fr.script.Calculator;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.utils.FormulaUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Handsome on 2018/2/1 0001 15:06
 */
public class ColumnFormulaOperatorResultSet implements SwiftResultSet {

    private ColumnType columnType;
    private String expression;
    private Segment[] segment;
    private int rowCursor;
    private int segCursor;
    private int rowCount;
    private Calculator cal;
    private String formula;
    private TempValue tempValue;
    private SwiftMetaData metaData;
//    //1900到1970年的天数
//    private static final long days = 25569;
//    private static final long msOfOneDay = 86400000;

    public ColumnFormulaOperatorResultSet(ColumnType columnType, String expression, Segment[] segment, SwiftMetaData metaData) {
        this.columnType = columnType;
        this.expression = expression;
        this.segment = segment;
        this.metaData = metaData;
        init();
    }

    private void init() {
        this.rowCursor = 0;
        this.segCursor = 0;
        this.rowCount = this.segment[0].getRowCount();
        cal = Calculator.createCalculator();
        formula = FormulaUtils.getParameterIndexEncodedFormula(expression);
        tempValue = new TempValue();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNext() {
        if (this.segCursor < this.segment.length && this.rowCursor < this.rowCount) {
            rowCount = segment[segCursor].getRowCount();
            Map<String, ColumnKey> columnKeyMap = FormulaUtils.createColumnIndexMap(expression, segment[segCursor]);
            try {
                Object value = FormulaUtils.getCalculatorValue(cal, formula, segment[segCursor], columnKeyMap, rowCursor);
                List list = new ArrayList();
                list.add(getValueByColumnType(value));
                tempValue.setRow(new ListBasedRow(list));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (rowCursor < segment[segCursor].getRowCount() - 1) {
                rowCursor++;
            } else {
                if (segCursor < segment.length) {
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
    public Row getNextRow() {
        return tempValue.getRow();
    }

    private Object getValueByColumnType(Object value) {
        if (null == value) {
            return null;
        }
        switch (columnType) {
            case DATE:
                return object2Date(value);
            case NUMBER:
                return object2Number(value);
            default:
                return Utils.objectToString(value);
        }
    }

    private Long object2Date(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
            //所有原始字段（文本/数值/时间）为空的，转换字段类型后也是空。原始数值字段为null的，转换字段类型后也为空
        } else if (DateUtils.object2Date(value, true) == null) {
            return null;
        } else {
            return DateUtils.object2Date(value, true).getTime();
        }
    }

    private Double object2Number(Object value) {
        if (value instanceof Date) {
            return (double) (((Date) value).getTime());
            //所有原始字段（文本/数值/时间）为空的，转换字段类型后也是空。原始数值字段为null的，转换字段类型后也为空
        } else if (Utils.objectToNumber(value, true) == null) {
            return null;
        } else {
            return Utils.objectToNumber(value, true).doubleValue();
        }
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
}