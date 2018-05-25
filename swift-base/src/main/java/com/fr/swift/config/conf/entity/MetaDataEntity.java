package com.fr.swift.config.conf.entity;

import com.fr.decision.webservice.utils.DecisionServiceConstants;
import com.fr.swift.config.conf.bean.Convert;
import com.fr.swift.config.conf.convert.MetaDataColumnListConverter;
import com.fr.swift.config.conf.bean.MetaDataColumnBean;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
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
public class MetaDataEntity extends com.fr.config.entity.Entity implements Convert<SwiftMetaDataBean> {

    @Column(name = "schema")
    private String schema;

    @Column(name = "tableName")
    private String tableName;

    @Column(name = "remark")
    private String remark;

    @Column(name = "fields", length = DecisionServiceConstants.LONG_TEXT_LENGTH)
    @com.fr.third.javax.persistence.Convert(
            converter = MetaDataColumnListConverter.class
    )
    private List<SwiftMetaDataColumn> fields;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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
        return new SwiftMetaDataBean(getId(), schema, tableName, remark, fields);
    }
}
