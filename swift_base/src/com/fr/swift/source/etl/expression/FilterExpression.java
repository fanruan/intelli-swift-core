package com.fr.swift.source.etl.expression;

import com.finebi.conf.structure.filtervalue.FineFilterValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * Created by Handsome on 2018/3/1 0001 16:17
 */
public class FilterExpression implements Expression {

    private FineFilterValue filter;
    private ColumnKey columnKey;
    private String fieldValue;
    private int fieldType;
    private Object value;
    private Object tValue;
    private int type;

    public FilterExpression(FineFilterValue filter, ColumnKey columnKey, String fieldValue,
                            int fieldType, Object Value, Object tValue, int type) {
        this.filter = filter;
        this.columnKey = columnKey;
        this.fieldValue = fieldValue;
        this.fieldType = fieldType;
        this.value = value;
        this.tValue = tValue;
        this.type = type;
    }

    private ColumnKey getColumnKey() {
        if(null == columnKey) {
            columnKey = new ColumnKey(fieldValue);
        }
        return columnKey;
    }

    @Override
    public Object get(Segment segment, int row, int columnType) {
        DictionaryEncodedColumn getter = segment.getColumn(getColumnKey()).getDictionaryEncodedColumn();
        Object v = getter.getValue(getter.getIndexByRow(row));
        //if(filter.)
        return null;
    }
}
