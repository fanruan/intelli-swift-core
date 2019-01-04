package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
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
    private AggregatorType calculatorType;

    public AllDataRowCalculatorOperator(String addedColumnName, ColumnType columnType, String columnName,
                                        ColumnKey[] dimension, AggregatorType calculatorType) {
        this.addedColumnName = addedColumnName;
        this.columnType = columnType;
        this.columnName = columnName;
        this.dimension = dimension;
        this.calculatorType = calculatorType;
    }

    public ColumnKey[] getDimension() {
        return dimension;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public AggregatorType getCalculatorType() {
        return calculatorType;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(this.addedColumnName, this.addedColumnName,
                Types.DOUBLE, fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(addedColumnName);
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ALL_DATA;
    }

}
