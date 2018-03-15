package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/24 0024 14:49
 */
public class AllDataRowCalculatorOperator extends AbstractOperator {
    @CoreField
    private String addedColumnName;//新增列
    @CoreField
    private ColumnType columnType;
    @CoreField
    private String columnName;
    @CoreField
    private ColumnKey[] dimension;
    @CoreField
    private int calculatorType;

    public AllDataRowCalculatorOperator(String addedColumnName, ColumnType columnType, String columnName,
                                        ColumnKey[] dimension, int calculatorType) {
        this.addedColumnName = addedColumnName;
        this.columnType = columnType;
        this.columnName = columnName;
        this.dimension = dimension;
        this.calculatorType = calculatorType;
    }
    public ColumnKey[] getDimension() {
        return dimension;
    }

    public String getAddedColumnName() { return addedColumnName; }

    public String getColumnName() {
        return columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public int getCalculatorType() { return calculatorType; }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(MD5Utils.getMD5String(new String[]{(this.addedColumnName)}),
                this.addedColumnName, ColumnTypeUtils.columnTypeToSqlType(this.columnType), true));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ALLDATA;
    }

    public String getColumnMD5() {
        return MD5Utils.getMD5String(new String[]{(this.addedColumnName)});
    }
}
