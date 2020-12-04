package com.fr.swift.config.entity;

import com.fr.swift.config.entity.convert.MetaDataColumnListConverter;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataColumnAbsentException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Assert;
import com.fr.swift.util.Strings;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
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
public class SwiftMetaDataEntity implements SwiftMetaData, Serializable {
    private static final long serialVersionUID = -6185911493489618460L;
    /**
     * id实际上传的是SourceKey
     * 理论上SourceKey不重复
     */
    @Id
    protected String id;
    @Column(name = "swiftSchema")
    @Enumerated(EnumType.STRING)
    protected SwiftDatabase swiftDatabase;
    @Column(name = "schemaName")
    protected String schemaName;
    @Column(name = "tableName")
    protected String tableName;
    @Column(name = "remark")
    protected String remark;
    @Column(name = "fields", length = 65536)
    @Convert(
            converter = MetaDataColumnListConverter.class
    )
    protected List<SwiftMetaDataColumn> fields;

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataEntity(String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(tableName, null, null, fieldList);
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataEntity(String tableName, String remark, String schemaName, List<SwiftMetaDataColumn> fields) {
        this(null, SwiftDatabase.CUBE, schemaName, tableName, remark, fields);
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataEntity(String id, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this(id, SwiftDatabase.CUBE, schemaName, tableName, remark, fields);
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataEntity(String id, SwiftDatabase swiftSchema, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this.id = id;
        this.swiftDatabase = swiftSchema;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fields;
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataEntity() {
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

    /**
     * @param columnName
     * @return -1:表示字段不存在
     * @throws SwiftMetaDataException
     * @descrption 同时规避无脑循环和并发初始化问题。
     */
    @Override
    public int getColumnIndex(String columnName) throws SwiftMetaDataException {
        if (Strings.isEmpty(columnName)) {
            throw new SwiftMetaDataColumnAbsentException(tableName, columnName);
        }
        for (int i = 1; i <= getColumnCount(); i++) {
            if (getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
        //throw new SwiftMetaDataColumnAbsentException(tableName, columnName);
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
    public SwiftMetaData clone() {
        return new Builder(this).build();
    }

    public static class Builder {
        private SwiftMetaDataEntity meta = new SwiftMetaDataEntity();

        public Builder() {
            meta.fields = new ArrayList<>();
        }

        public Builder(SwiftMetaData meta) {
            try {
                this.meta.id = meta.getId();
                this.meta.schemaName = meta.getSchemaName();
                this.meta.swiftDatabase = meta.getSwiftDatabase();
                this.meta.tableName = meta.getTableName();
                this.meta.fields = new ArrayList<>();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    this.meta.fields.add(new MetaDataColumnEntity.Builder(meta.getColumn(i + 1)).build());
                }
                this.meta.remark = meta.getRemark();
            } catch (SwiftMetaDataException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder setId(String id) {
            meta.id = id;
            return this;
        }

        public Builder setSwiftSchema(SwiftDatabase schema) {
            meta.swiftDatabase = schema;
            return this;
        }

        public Builder setTableName(String tableName) {
            meta.tableName = tableName;
            return this;
        }

        public Builder setRemark(String remark) {
            meta.remark = remark;
            return this;
        }

        public Builder setFields(List<SwiftMetaDataColumn> fields) {
            meta.fields = fields;
            return this;
        }

        public Builder addField(SwiftMetaDataColumn columnMeta) {
            meta.fields.add(columnMeta);
            return this;
        }

        private String genId() {
            return meta.tableName;
//            return String.format("%s.%s", meta.swiftSchema.getName(), meta.tableName);
        }

        public SwiftMetaDataEntity build() {
            Assert.notNull(meta.swiftDatabase);
            Assert.hasText(meta.tableName);
            Assert.notEmpty(meta.fields);
            if (meta.id == null) {
                meta.id = genId();
            }
            return meta;
        }
    }
}