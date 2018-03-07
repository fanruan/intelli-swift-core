package com.fr.swift.source.etl.date;

import com.fr.swift.source.*;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/2 0002 17:22
 */
public class GetFromDateOperator extends AbstractOperator {

    private String field;
    private int type;
    private String columnName;//新增列名
    private int columnType;

    public GetFromDateOperator(String field, int type, String columnName, int columnType) {
        this.field = field;
        this.type = type;
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public String getField() {
        return field;
    }

    public int getType() {
        return type;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(columnName, getSqlType(metaDatas)));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.GETDATE;
    }

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }
}
