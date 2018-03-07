package com.fr.swift.source.etl.rowcal.accumulate;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 15:23
 */
public class AccumulateRowOperator extends AbstractOperator {

    private ColumnKey columnKey;
    private String columnName;//新增列名
    private int columnType;
    private ColumnKey[] dimension;

    public AccumulateRowOperator(ColumnKey columnKey, String columnName, int columnType, ColumnKey[] dimension) {
        this.columnKey = columnKey;
        this.columnName = columnName;
        this.columnType = columnType;
        this.dimension = dimension;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public ColumnKey[] getDimension() {
        return dimension;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(columnName, getSqlType(metaDatas)));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ACCUMULATE;
    }

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }
}
