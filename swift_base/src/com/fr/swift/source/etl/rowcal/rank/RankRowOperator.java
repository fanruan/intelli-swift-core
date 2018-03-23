package com.fr.swift.source.etl.rowcal.rank;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.sql.Types;
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

    public ColumnKey getColumnKey() {
        return columnKey;
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
        columnList.add(new MetaDataColumn(this.columnName, this.columnName,
                Types.INTEGER, ColumnTypeUtils.MAX_LONG_COLUMN_SIZE, 0, fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.RANK;
    }

}
