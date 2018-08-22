package com.fr.swift.source.meta;

import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * Created by lyon on 2018/8/22.
 */
public class SwiftMetaDataImpl implements SwiftMetaData {

    private String tableName;
    private SwiftDatabase.Schema schema;
    private List<String> fieldNames;
    private List<SwiftMetaDataColumn> metaDataColumns;

    public SwiftMetaDataImpl(String tableName, SwiftDatabase.Schema schema, List<String> fieldNames,
                             List<SwiftMetaDataColumn> metaDataColumns) {
        this.tableName = tableName;
        this.schema = schema;
        this.fieldNames = fieldNames;
        this.metaDataColumns = metaDataColumns;
    }

    @Override
    public SwiftDatabase.Schema getSwiftSchema() {
        return schema;
    }

    @Override
    public String getSchemaName() {
        return schema.getName();
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getRemark() {
        return null;
    }

    @Override
    public int getColumnCount() {
        return fieldNames.size();
    }

    @Override
    public String getColumnName(int index) {
        return fieldNames.get(index - 1);
    }

    @Override
    public String getColumnRemark(int index) {
        return null;
    }

    @Override
    public int getColumnType(int index) {
        return metaDataColumns.get(index - 1).getType();
    }

    @Override
    public int getPrecision(int index) {
        return metaDataColumns.get(index - 1).getPrecision();
    }

    @Override
    public int getScale(int index) {
        return metaDataColumns.get(index - 1).getScale();
    }

    @Override
    public SwiftMetaDataColumn getColumn(int index) {
        return metaDataColumns.get(index - 1);
    }

    @Override
    public SwiftMetaDataColumn getColumn(String columnName) {
        return metaDataColumns.get(fieldNames.indexOf(columnName));
    }

    @Override
    public int getColumnIndex(String columnName) {
        int index = fieldNames.indexOf(columnName);
        if (index == -1) {
            Crasher.crash(new SwiftMetaDataException());
        }
        return index + 1;
    }

    @Override
    public String getColumnId(int index) {
        return fieldNames.get(index - 1);
    }

    @Override
    public String getColumnId(String columnName) {
        return null;
    }

    @Override
    public List<String> getFieldNames() {
        return fieldNames;
    }
}
