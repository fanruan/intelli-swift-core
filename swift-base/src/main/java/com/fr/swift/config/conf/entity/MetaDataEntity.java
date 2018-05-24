package com.fr.swift.config.conf.entity;

import com.fr.decision.base.entity.BaseEntity;
import com.fr.swift.config.conf.bean.Convert;
import com.fr.swift.config.conf.convert.MetaDataColumnListConverter;
import com.fr.swift.config.conf.bean.MetaDataColumnBean;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
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
public class MetaDataEntity extends BaseEntity implements Convert<SwiftMetaDataBean> {

    @Column(name = "schema")
    private String schema;

    @Column(name = "tableName")
    private String tableName;

    @Column(name = "remark")
    private String remark;

    @Column(name = "fields")
    @com.fr.third.javax.persistence.Convert(
            converter = MetaDataColumnListConverter.class
    )
    private List<MetaDataColumnBean> fields;

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

    public List<MetaDataColumnBean> getFields() {
        return fields;
    }

    public void setFields(List<MetaDataColumnBean> fields) {
        this.fields = fields;
    }

    @Override
    public SwiftMetaDataBean convert() {
        return new SwiftMetaDataBean(getId(), schema, tableName, remark, fields);
    }
}
