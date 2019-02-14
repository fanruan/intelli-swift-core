package com.fr.swift.source.etl.formula;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Handsome on 2018/2/2 0002 10:07
 */
public class ColumnFormulaOperator extends AbstractOperator {

    @CoreField
    private String columnName;
    @CoreField
    private ColumnType columnType;
    @CoreField
    private String expression;

    public ColumnFormulaOperator(String columnName, ColumnType columnType, String expression) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.expression = expression;
    }


    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumnBean(this.columnName,
                this.columnName, ColumnTypeUtils.columnTypeToSqlType(this.columnType), MD5Utils.getMD5String(new String[]{(this.expression + this.columnType)})));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.COLUMN_FORMULA;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public List<String> getNewAddedName() {
        return Collections.singletonList(columnName);
    }
}