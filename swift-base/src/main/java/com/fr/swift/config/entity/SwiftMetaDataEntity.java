package com.fr.swift.config.entity;

import com.fr.decision.webservice.utils.DecisionServiceConstants;
import com.fr.swift.config.bean.Convert;
import com.fr.swift.config.convert.MetaDataColumnListConverter;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

import java.util.List;


/**
 * @author yee
 * @date 2018/5/24
 */
@Entity
@Table(name = "swift_metadata")
public class SwiftMetaDataEntity extends com.fr.config.entity.Entity implements Convert<SwiftMetaDataBean> {

    @Column(name = "schemaName")
    private String schemaName;

    @Column(name = "tableName")
    private String tableName;

    @Column(name = "remark")
    private String remark;

    @Column(name = "fields", length = DecisionServiceConstants.LONG_TEXT_LENGTH)
    @com.fr.third.javax.persistence.Convert(
            converter = MetaDataColumnListConverter.class
    )
    private List<SwiftMetaDataColumn> fields;

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

    @Override
    public SwiftMetaDataBean convert() {
        return new SwiftMetaDataBean(getId(), schemaName, tableName, remark, fields);
    }
}
