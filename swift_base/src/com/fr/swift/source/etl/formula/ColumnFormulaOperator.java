package com.fr.swift.source.etl.formula;

import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/2 0002 10:07
 */
public class ColumnFormulaOperator extends AbstractOperator {

    private String columnName;
    private int columnType;
    private String expression;

    public ColumnFormulaOperator(String columnName, int columnType, String expression) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.expression = expression;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(this.columnName, ColumnTypeUtils.columnTypeToSqlType(this.columnType)));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.COLUMNFORMULA;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public String getExpression() {
        return expression;
    }
}
