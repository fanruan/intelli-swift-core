package com.fr.swift.source.etl.rowcal.correspondperiodpercentage;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/4 0006 11:49
 */
public class CorrespondMonthPercentOperator extends AbstractOperator {

    private String columnName;//新增列名
    private int columnType;
    private ColumnKey columnKey;
    private ColumnKey periodKey;
    private ColumnKey[] dimensions;

    public CorrespondMonthPercentOperator(String columnName, int columnType, ColumnKey columnKey,
                                          ColumnKey periodKey, ColumnKey[] dimensions) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnKey = columnKey;
        this.periodKey = periodKey;
        this.dimensions = dimensions;
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

    @Override
    public List<String> getNewAddedName() {
        List<String> addColumnNames = new ArrayList<String>();
        addColumnNames.add(columnName);
        return addColumnNames;
    }

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
        return OperatorType.PERCENTAGE;
    }
}
