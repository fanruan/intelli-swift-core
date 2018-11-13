package com.fr.swift.config.bean;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class SwiftMetaDataBean implements SwiftMetaData, Serializable, Convert<SwiftMetaDataEntity> {
    private static final long serialVersionUID = -6185911493489618460L;
    /**
     * id实际上传的是SourceKey
     * 理论上SourceKey不重复
     */
    @JsonProperty
    private String id;
    @JsonProperty
    private SwiftDatabase swiftDatabase;
    @JsonProperty
    private String schemaName;
    @JsonProperty
    private String tableName;
    @JsonProperty
    private String remark;
    @JsonProperty
    private List<SwiftMetaDataColumn> fields;
    @JsonProperty
    private int columnCount;

    public SwiftMetaDataBean(String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(tableName, null, null, fieldList);
    }

    public SwiftMetaDataBean(SwiftDatabase swiftDatabase, String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(null, swiftDatabase, null, tableName, null, fieldList);
    }

    public SwiftMetaDataBean(String tableName, String remark, String schemaName, List<SwiftMetaDataColumn> fields) {
        this(null, SwiftDatabase.CUBE, schemaName, tableName, remark, fields);
    }

    public SwiftMetaDataBean(String id, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this(id, SwiftDatabase.CUBE, schemaName, tableName, remark, fields);
    }

    public SwiftMetaDataBean(SwiftMetaDataEntity metaEntity) {
        this(metaEntity.getId(), metaEntity.getSwiftSchema(), metaEntity.getSchemaName(), metaEntity.getTableName(), metaEntity.getRemark(), metaEntity.getFields());
    }

    public SwiftMetaDataBean(String id, SwiftDatabase swiftDatabase, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this.id = id;
        this.swiftDatabase = swiftDatabase;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fields;
        this.columnCount = fields.size();
    }

    public SwiftMetaDataBean() {
    }

    @Override
    public SwiftDatabase getSwiftDatabase() {
        return swiftDatabase;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
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
        throw new SwiftMetaDataColumnAbsentException(tableName, columnName);
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
        throw new SwiftMetaDataColumnAbsentException(tableName, columnName);
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
        if (null != fields) {
            for (SwiftMetaDataColumn column : fields) {
                fieldNames.add(column.getName());
            }
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

    public void setSwiftDatabase(SwiftDatabase schema) {
        this.swiftDatabase = schema;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, %s}", swiftDatabase, tableName, fields);
    }

    @Override
    public SwiftMetaDataEntity convert() {
        return new SwiftMetaDataEntity(this);
    }
}
