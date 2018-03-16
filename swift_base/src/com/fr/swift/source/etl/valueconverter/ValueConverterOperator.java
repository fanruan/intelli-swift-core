package com.fr.swift.source.etl.valueconverter;

import com.fr.swift.source.*;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/22 0022 17:31
 */
public class ValueConverterOperator extends AbstractOperator {

    private String column;
    private int columnType;
    private String columnName;

    public ValueConverterOperator(String column, int columnType, String columnName) {
        this.column = column;
        this.columnType = columnType;
        this.columnName = columnName;
    }

    public String getColumn() {
        return column;
    }

    public int getColumnType() {
        return columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getNewAddedName() {
        return columnName;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(columnName, getSqlType(metaDatas)));
        return null;
    }

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.CONVERTER;
    }
}
