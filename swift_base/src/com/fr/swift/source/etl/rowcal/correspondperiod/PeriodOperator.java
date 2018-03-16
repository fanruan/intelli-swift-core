package com.fr.swift.source.etl.rowcal.correspondperiod;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/5 0006 16:13
 */
public class PeriodOperator extends AbstractOperator {

    private String columnName;//新增列名
    private int columnType;
    private ColumnKey columnKey;
    private ColumnKey periodKey;
    private ColumnKey[] dimensions;//month作为最后一项

    public PeriodOperator(String columnName, int columnType, ColumnKey columnKey, ColumnKey periodKey, ColumnKey[] dimenions) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnKey = columnKey;
        this.periodKey = periodKey;
        this.dimensions = dimenions;
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

    public ColumnKey getPeriodKey() {
        return periodKey;
    }

    public ColumnKey[] getDimensions() {
        return dimensions;
    }

    public String getNewAddedName() {
        return columnName;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(columnName, getSqlType(metaDatas)));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.PERIOD;
    }

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }
}
