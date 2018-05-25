package com.fr.swift.config.conf.bean;

import com.fr.decision.base.data.BaseDataRecord;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.entity.MetaDataEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class SwiftMetaDataBean extends BaseDataRecord implements IMetaData<MetaDataColumnBean>, Serializable, Convert<MetaDataEntity> {

    public static final String COLUMN_SCHEMA = "schema";
    public static final String COLUMN_TABLE_NAME = "tableName";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_FIELDS = "fields";

    private String schema;

    private String tableName;

    private String remark;

    private List<MetaDataColumnBean> fields;

    public SwiftMetaDataBean(String schema, String tableName, String remark, List<MetaDataColumnBean> fieldList) {
        this.schema = schema;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fieldList;
    }

    public SwiftMetaDataBean(String id, String schema, String tableName, String remark, List<MetaDataColumnBean> fields) {
        this.setId(id);
        this.schema = schema;
        this.tableName = tableName;
        this.remark = remark;
        this.fields = fields;
    }

    public SwiftMetaDataBean() {
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public List<MetaDataColumnBean> getFields() {
        return fields;
    }

    @Override
    public void setFields(List<MetaDataColumnBean> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "{" + tableName + ", " + fields + "}";
    }

    @Override
    public MetaDataEntity convert() {
        MetaDataEntity entity = new MetaDataEntity();
        entity.setId(getId());
        entity.setTableName(getTableName());
        entity.setSchema(getSchema());
        entity.setRemark(getRemark());
        entity.setFields(getFields());
        return entity;
    }
}
