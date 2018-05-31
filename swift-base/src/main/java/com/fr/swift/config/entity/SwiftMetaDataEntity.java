package com.fr.swift.config.entity;

import com.fr.swift.config.SwiftConfigConstants.MetaDataConfig;
import com.fr.swift.config.bean.Convert;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.convert.MetaDataColumnListConverter;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.db.impl.SwiftDatabase.Schema.SchemaConverter;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;

import java.util.List;

import static com.fr.decision.webservice.utils.DecisionServiceConstants.LONG_TEXT_LENGTH;

/**
 * @author yee
 * @date 2018/5/24
 */
@Entity
@Table(name = "FINE_SWIFT_METADATA")
public class SwiftMetaDataEntity implements Convert<SwiftMetaDataBean> {
    @Id
    private String id;

    @Column(name = MetaDataConfig.COLUMN_SWIFT_SCHEMA)
    @com.fr.third.javax.persistence.Convert(
            converter = SchemaConverter.class
    )
    private Schema swiftSchema;

    @Column(name = MetaDataConfig.COLUMN_SCHEMA)
    private String schemaName;

    @Column(name = MetaDataConfig.COLUMN_TABLE_NAME)
    private String tableName;

    @Column(name = MetaDataConfig.COLUMN_REMARK)
    private String remark;

    @Column(name = MetaDataConfig.COLUMN_FIELDS, length = LONG_TEXT_LENGTH)
    @com.fr.third.javax.persistence.Convert(
            converter = MetaDataColumnListConverter.class
    )
    private List<SwiftMetaDataColumn> fields;

    public SwiftMetaDataEntity(SwiftMetaDataBean metaBean) {
        this.id = metaBean.getId();
        this.swiftSchema = metaBean.getSwiftSchema();
        this.schemaName = metaBean.getSchemaName();
        this.tableName = metaBean.getTableName();
        this.remark = metaBean.getRemark();
        this.fields = metaBean.getFields();
    }

    public SwiftMetaDataEntity() {
    }

    public Schema getSwiftSchema() {
        return swiftSchema;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRemark() {
        return remark;
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
    public SwiftMetaDataBean convert() {
        return new SwiftMetaDataBean(this);
    }
}
