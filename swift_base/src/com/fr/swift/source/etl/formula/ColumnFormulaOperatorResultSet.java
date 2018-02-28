package com.fr.swift.source.etl.formula;

import com.fr.base.Utils;
import com.fr.script.Calculator;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.FormulaUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String expressionStr;
    private String formula;
    private TempValue tempValue;
    private SwiftMetaData metaData;

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
        expressionStr = expression;
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(expressionStr);
        int parameterCount = 0;
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            expressionStr = expressionStr.replace(matchStr, "$" + String.valueOf(parameterCount));
            parameterCount++;
        }
        formula = "=" + expressionStr;
        tempValue = new TempValue();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean next() {
        if (this.segCursor < this.segment.length && this.rowCursor < this.rowCount) {
            rowCount = segment[segCursor].getRowCount();
            Map<String, ColumnKey> columnKeyMap = FormulaUtils.createColumnIndexMap(expression, segment[segCursor]);
            try {
                Object value = FormulaUtils.getCalculatorValue(cal, formula, segment[segCursor], columnKeyMap, rowCursor);
                List list = new ArrayList();
                SwiftMetaData metaData = segment[segCursor].getMetaData();
                list.add(getValueByColumnType(value));
                if (!segment[segCursor].isHistory()) {
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        DictionaryEncodedColumn getter = segment[segCursor].getColumn(new ColumnKey(metaData.getColumnName(i + 1))).getDictionaryEncodedColumn();
                        Object ob = getter.getValue(getter.getIndexByRow(rowCursor));
                        list.add(ob);
                    }
                }
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
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() {
        return tempValue.getRow();
    }

    private Object getValueByColumnType(Object value) {
        if (null == value) {
            return null;
        }
        switch (columnType) {
            case DATE:
                return ((Date) value).getTime();
            case NUMBER:
                return ((Number) value).doubleValue();
            default:
                return Utils.objectToString(value);
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
}