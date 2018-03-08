package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.etl.utils.ETLConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/24 0024 14:49
 */
public class AllDataRowCalculatorOperator extends AbstractOperator {

    private int summaryType = ETLConstant.CONF.GROUP.NUMBER.SUM;
    private String addedColumnName;//新增列
    private int columnType;
    private String columnName;
    private ColumnKey[] dimension;

    public AllDataRowCalculatorOperator(String addedColumnName, int columnType, String columnName, ColumnKey[] dimension) {
        this.addedColumnName = addedColumnName;
        this.columnType = columnType;
        this.columnName = columnName;
        this.dimension = dimension;
    }
    public ColumnKey[] getDimension() {
        return dimension;
    }

    public String getAddedColumnName() { return addedColumnName; }

    public int getSummaryType() {
        return summaryType;
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
        columnList.add(new MetaDataColumn(addedColumnName, getSqlType(metaDatas)));
        return columnList;
    }

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ALLDATA;
    }
}
