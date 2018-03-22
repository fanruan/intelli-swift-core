package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/24 0024 14:49
 */
public class AllDataRowCalculatorOperator extends AbstractOperator {
    private String addedColumnName;//新增列
    @CoreField
    private ColumnType columnType;
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

    public String getAddedColumnName() {
        return addedColumnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public int getCalculatorType() {
        return calculatorType;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(this.addedColumnName, this.addedColumnName,
                ColumnTypeUtils.columnTypeToSqlType(this.columnType), fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public List<String> getNewAddedName() {
        List<String> addColumnNames = new ArrayList<String>();
        addColumnNames.add(addedColumnName);
        return addColumnNames;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ALLDATA;
    }

    public String getColumnMD5() {
        return MD5Utils.getMD5String(new String[]{(this.addedColumnName)});
    }
}
