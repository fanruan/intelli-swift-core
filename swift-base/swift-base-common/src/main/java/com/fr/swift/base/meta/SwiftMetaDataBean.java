package com.fr.swift.base.meta;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Convert;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Enumerated;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.annotation.persistence.Table;
import com.fr.swift.config.convert.MetaDataColumnListConverter;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
@Entity
@Table(name = "fine_swift_metadata")
public class SwiftMetaDataBean implements SwiftMetaData, Serializable {
    private static final long serialVersionUID = -6185911493489618460L;
    /**
     * id实际上传的是SourceKey
     * 理论上SourceKey不重复
     */
    @Id
    private String id;
    @Column(name = "swiftSchema")
    @Enumerated(Enumerated.EnumType.STRING)
    private SwiftSchema swiftSchema;
    @Column(name = "schemaName")
    private String schemaName;
    @Column(name = "tableName")
    private String tableName;
    @Column(name = "remark")
    private String remark;
    @Column(name = "fields", length = 65536)
    @Convert(converter = MetaDataColumnListConverter.class)
    private List<SwiftMetaDataColumn> fields;

    public SwiftMetaDataBean(String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(tableName, null, null, fieldList);
    }

    public SwiftMetaDataBean(SwiftSchema swiftSchema, String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(null, swiftSchema, null, tableName, null, fieldList);
    }

    public SwiftMetaDataBean(String tableName, String remark, String schemaName, List<SwiftMetaDataColumn> fields) {
        this(null, SwiftSchema.CUBE, schemaName, tableName, remark, fields);
    }

    public SwiftMetaDataBean(String id, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this(id, SwiftSchema.CUBE, schemaName, tableName, remark, fields);
    }

    public SwiftMetaDataBean(String id, SwiftSchema swiftSchema, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this.id = id;
        this.swiftSchema = swiftSchema;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fields;
    }

    public SwiftMetaDataBean() {
    }

    @Override
    public SwiftSchema getSwiftSchema() {
        return swiftSchema;
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
        return fields.size();
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
        if (index < 1 || index > getColumnCount()) {
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
            for (int i = 0, columnCount = getColumnCount(); i < columnCount; i++) {
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
    }

    public void setSwiftSchema(SwiftSchema schema) {
        this.swiftSchema = schema;
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
        return String.format("{%s, %s, %s}", swiftSchema, tableName, fields);
    }

    @Override
    public SwiftMetaData clone() {
        SwiftMetaDataBean bean = new SwiftMetaDataBean();
        bean.id = this.id;
        bean.remark = this.remark;
        bean.schemaName = this.schemaName;
        bean.swiftSchema = this.swiftSchema;
        bean.tableName = this.tableName;
        bean.fields = new ArrayList<SwiftMetaDataColumn>();
        for (SwiftMetaDataColumn field : this.fields) {
            bean.fields.add(new MetaDataColumnBean(field.getName(), field.getRemark(), field.getType(), field.getPrecision(), field.getScale(), field.getColumnId()));
        }
        return bean;
    }
}
