package com.fr.swift.source.etl.rowcal.accumulate;

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
 * Created by Handsome on 2018/2/28 0028 15:23
 */
public class AccumulateRowOperator extends AbstractOperator {
    @CoreField
    private ColumnKey columnKey;
    @CoreField
    private String columnName;//新增列名
    @CoreField
    private ColumnType columnType;
    @CoreField
    private ColumnKey[] dimension;

    public AccumulateRowOperator(ColumnKey columnKey, String columnName, ColumnType columnType, ColumnKey[] dimension) {
        this.columnKey = columnKey;
        this.columnName = columnName;
        this.columnType = columnType;
        this.dimension = dimension;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public ColumnKey[] getDimension() {
        return dimension;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(this.columnName, this.columnName,
                ColumnTypeUtils.columnTypeToSqlType(this.columnType), MD5Utils.getMD5String(new String[]{(this.columnName)})));
        return columnList;
    }

    @Override
    public String getNewAddedName() {
        return columnName;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.ACCUMULATE;
    }

    public String getColumnMD5() {
        return MD5Utils.getMD5String(new String[]{(this.columnName)});
    }
}
