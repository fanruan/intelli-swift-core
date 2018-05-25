package com.fr.swift.config.bean;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class SwiftMetaDataBean implements SwiftMetaData, Serializable, Convert<SwiftMetaDataEntity> {

    public static final String COLUMN_SCHEMA = "schema";
    public static final String COLUMN_TABLE_NAME = "tableName";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_FIELDS = "fields";
    public static final String COLUMN_ID = "id";

    private String id;

    private String schema;

    private String tableName;

    private String remark;

    private List<SwiftMetaDataColumn> fields;

    private int columnCount;

    public SwiftMetaDataBean(String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(tableName, null, null, fieldList);
    }

    public SwiftMetaDataBean(String tableName, String remark, String schema, List<SwiftMetaDataColumn> fieldList) {
        this.schema = schema;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fieldList;
        this.columnCount = fieldList.size();
    }

    public SwiftMetaDataBean(String id, String schema, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this.id = id;
        this.schema = schema;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fields;
        this.columnCount = fields.size();
    }

    public SwiftMetaDataBean() {
    }

    @Override
    public String getSchemaName() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public String getColumnName(int index) throws SwiftMetaDataException {
        return getColumn(index).getName();
    }

    @Override
    public String getColumnRemark(int index) throws SwiftMetaDataException {
        return getColumn(index).getRemark();
    }

    @Override
    public int getColumnType(int index) throws SwiftMetaDataException {
        return getColumn(index).getType();
    }

    @Override
    public int getPrecision(int index) throws SwiftMetaDataException {
        return getColumn(index).getPrecision();
    }

    @Override
    public int getScale(int index) throws SwiftMetaDataException {
        return getColumn(index).getScale();
    }

    @Override
    public SwiftMetaDataColumn getColumn(int index) throws SwiftMetaDataException {
        if (index < 1 || index > columnCount) {
            throw new SwiftMetaDataException();
        }
        return fields.get(index - 1);
    }

    @Override
    public SwiftMetaDataColumn getColumn(String columnName) throws SwiftMetaDataException {
        if (StringUtils.isNotEmpty(columnName)) {
            for (SwiftMetaDataColumn column : fields) {
                if (ComparatorUtils.equals(columnName, column.getName())) {
                    return column;
                }
            }
        }
        throw new SwiftMetaDataException();
    }

    @Override
    public int getColumnIndex(String columnName) throws SwiftMetaDataException {
        if (StringUtils.isNotEmpty(columnName)) {
            for (int i = 0; i < columnCount; i++) {
                SwiftMetaDataColumn column = fields.get(i);
                if (ComparatorUtils.equals(columnName, column.getName())) {
                    return i + 1;
                }
            }
        }
        throw new SwiftMetaDataException();
    }

    @Override
    public String getColumnId(int index) throws SwiftMetaDataException {
        return getColumn(index).getColumnId();
    }

    @Override
    public String getColumnId(String columnName) throws SwiftMetaDataException {
        return getColumn(columnName).getColumnId();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        for (SwiftMetaDataColumn column : fields) {
            fieldNames.add(column.getName());
        }
        return fieldNames;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<SwiftMetaDataColumn> getFields() {
        return fields;
    }

    public void setFields(List<SwiftMetaDataColumn> fields) {
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" + tableName + ", " + fields + "}";
    }

    @Override
    public SwiftMetaDataEntity convert() {
        SwiftMetaDataEntity entity = new SwiftMetaDataEntity();
        entity.setId(getId());
        entity.setTableName(getTableName());
        entity.setSchemaName(getSchemaName());
        entity.setRemark(getRemark());
        entity.setFields(getFields());
        return entity;
    }
}
