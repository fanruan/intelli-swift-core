package com.fr.swift.source.etl.expression;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Handsome on 2018/3/6 0013 11:40
 */
public class ExpressionFilterOperator extends AbstractOperator {

    private String columnName;//新增列名
    private int columnType;
    private ColumnKey columnKey;
    private int type;
    private FilterInfo[] filters;
    private Object[] values;
    private Object otherValue;

    public ExpressionFilterOperator(String columnName, int columnType, ColumnKey columnKey,
                                    int type, FilterInfo[] filters, Object[] values, Object otherValue) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnKey = columnKey;
        this.type = type;
        this.filters = filters;
        this.values = values;
        this.otherValue = otherValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public int getType() {
        return type;
    }

    public FilterInfo[] getFilters() {
        return filters;
    }

    public Object[] getValues() {
        return values;
    }

    public Object getOtherValue() {
        return otherValue;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(columnName, getSqlType(metaDatas)));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.EXPRESSION_FILTER;
    }

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }
}
