package com.fr.swift.source.etl.rowcal.rank;

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
 * Created by Handsome on 2018/2/28 0028 10:55
 */
public class RankRowOperator extends AbstractOperator {
    @CoreField
    private int type;
    @CoreField
    private ColumnKey columnKey;
    @CoreField
    private String columnName;//新增列
    @CoreField
    private ColumnType columnType;
    @CoreField
    private ColumnKey[] dimension;

    public RankRowOperator(String columnName, int type, ColumnType columnType, ColumnKey columnKey, ColumnKey[] dimension) {
        this.type = type;
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnKey = columnKey;
        this.dimension = dimension;
    }

    public ColumnKey[] getDimension() {
        return dimension;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getType() {
        return type;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public ColumnKey getColumnKey() { return columnKey; }

    public String getNewAddedName() {
        return columnName;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(this.columnName, this.columnName,
                ColumnTypeUtils.columnTypeToSqlType(this.columnType), MD5Utils.getMD5String(new String[]{(this.columnName)})));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.RANK;
    }

    public String getColumnMD5() {
        return MD5Utils.getMD5String(new String[]{(this.columnName)});
    }
}
