package com.fr.swift.source.etl.rowcal.alldata;

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

    private int summaryType = ETLConstant.SUMMARY_TYPE.SUM;
    private int rule = ETLConstant.TARGET_TYPE.SUM_OF_ALL;
    private String columnName;
    private int columnType;

    public AllDataRowCalculatorOperator(String columnName, int columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }


    public int getSummaryType() {
        return summaryType;
    }

    public int getRule() {
        return rule;
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

    private int getSqlType(SwiftMetaData[] metaDatas) {
        return ColumnTypeUtils.columnTypeToSqlType(ColumnTypeConstants.ColumnType.values()[columnType]);
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.DETAIL;
    }
}
