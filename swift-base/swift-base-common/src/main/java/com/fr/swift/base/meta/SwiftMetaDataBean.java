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
import com.fr.swift.util.Assert;
import com.fr.swift.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private transient Map<String, Integer> fieldIndexes = new HashMap<>();

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataBean(String tableName, List<SwiftMetaDataColumn> fieldList) {
        this(tableName, null, null, fieldList);
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataBean(String tableName, String remark, String schemaName, List<SwiftMetaDataColumn> fields) {
        this(null, SwiftSchema.CUBE, schemaName, tableName, remark, fields);
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataBean(String id, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this(id, SwiftSchema.CUBE, schemaName, tableName, remark, fields);
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
    public SwiftMetaDataBean(String id, SwiftSchema swiftSchema, String schemaName, String tableName, String remark, List<SwiftMetaDataColumn> fields) {
        this.id = id;
        this.swiftSchema = swiftSchema;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fields;
    }

    /**
     * @deprecated 换Builder
     */
    @Deprecated
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

    /**
     * @param columnName
     * @return
     * @throws SwiftMetaDataException
     * @descrption 同时规避无脑循环和并发初始化问题。
     */
    @Override
    public int getColumnIndex(String columnName) throws SwiftMetaDataException {
        if (Strings.isEmpty(columnName)) {
            throw new SwiftMetaDataColumnAbsentException(tableName, columnName);
        }
        if (fieldIndexes == null || fieldIndexes.size() != fields.size()) {
            synchronized (this) {
                if (fieldIndexes == null) {
                    fieldIndexes = new HashMap<>();
                }
                if (fieldIndexes.size() != fields.size()) {
                    fieldIndexes.clear();
                    for (int i = 0; i < fields.size(); i++) {
                        SwiftMetaDataColumn column = fields.get(i);
                        fieldIndexes.put(column.getName(), i + 1);
                    }
                }
            }
        }
        if (fieldIndexes.get(columnName) == null) {
            throw new SwiftMetaDataColumnAbsentException(tableName, columnName);
        }
        return fieldIndexes.get(columnName);
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
        return new Builder(this).build();
    }

    public static class Builder {
        private SwiftMetaDataBean meta = new SwiftMetaDataBean();

        public Builder() {
            meta.fields = new ArrayList<>();
        }

        public Builder(SwiftMetaData meta) {
            try {
                this.meta.id = meta.getId();
                this.meta.schemaName = meta.getSchemaName();
                this.meta.swiftSchema = meta.getSwiftSchema();
                this.meta.tableName = meta.getTableName();
                this.meta.fields = new ArrayList<>();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    this.meta.fields.add(new MetaDataColumnBean.Builder(meta.getColumn(i + 1)).build());
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

        public Builder setSwiftSchema(SwiftSchema schema) {
            meta.swiftSchema = schema;
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
            return String.format("%s.%s", meta.swiftSchema.getName(), meta.tableName);
        }

        public SwiftMetaDataBean build() {
            Assert.notNull(meta.swiftSchema);
            Assert.hasText(meta.tableName);
            Assert.notEmpty(meta.fields);
            if (meta.id == null) {
                meta.id = genId();
            }
            return meta;
        }
    }
}