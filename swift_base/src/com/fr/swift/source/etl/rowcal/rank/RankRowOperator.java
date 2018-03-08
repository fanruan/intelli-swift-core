package com.fr.swift.source.etl.rowcal.rank;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/28 0028 10:55
 */
public class RankRowOperator extends AbstractOperator {

    private int type;
    private ColumnKey columnKey;
    private String columnName;//新增列
    private int columnType;
    private ColumnKey[] dimension;

    public RankRowOperator(String columnName, int type, int columnType, ColumnKey columnKey, ColumnKey[] dimension) {
        this.type = type;
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnKey = columnKey;
        this.dimension = dimension;
    }

    public ColumnKey[] getDimension() {
        return dimension;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getType() {
        return type;
    }

    public int getColumnType() {
        return columnType;
    }

    private ColumnKey getColumnKey() { return columnKey; }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(columnName, getSqlType(metaDatas)));
        return columnList;
    }

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.RANK;
    }
}
