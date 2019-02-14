package com.fr.swift.base.meta;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Strings;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class SwiftMetaDataBean implements SwiftMetaData, Serializable, ObjectConverter {
    private static final long serialVersionUID = -6185911493489618460L;
    /**
     * id实际上传的是SourceKey
     * 理论上SourceKey不重复
     */
    private String id;
    private SwiftDatabase swiftDatabase;
    private String schemaName;
    private String tableName;
    private String remark;
    private List<SwiftMetaDataColumn> fields;
    private int columnCount;

    public static final Class TYPE = entityType();

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.config.entity.SwiftMetaDataEntity");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

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

    public SwiftMetaDataBean(String id, SwiftDatabase swiftDatabase, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this.id = id;
        this.swiftDatabase = swiftDatabase;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fields;
        if (null == fields) {
            this.columnCount = 0;
        } else {
            this.columnCount = fields.size();
        }
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
        if (Strings.isNotEmpty(columnName)) {
            for (SwiftMetaDataColumn column : fields) {
                if (columnName.equals(column.getName())) {
                    return column;
                }
            }
        }
        throw new SwiftMetaDataColumnAbsentException(tableName, columnName);
    }

    @Override
    public int getColumnIndex(String columnName) throws SwiftMetaDataException {
        if (Strings.isNotEmpty(columnName)) {
            for (int i = 0; i < columnCount; i++) {
                SwiftMetaDataColumn column = fields.get(i);
                if (columnName.equals(column.getName())) {
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
        this.columnCount = fields.size();
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
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(SwiftMetaDataBean.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
