package com.fr.swift.config.pojo;

import com.fr.swift.config.IMetaData;

import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class SwiftMetaDataPojo implements IMetaData<MetaDataColumnPojo> {

    private String schema;

    private String tableName;

    private String remark;

    private List<MetaDataColumnPojo> fieldList;

    public SwiftMetaDataPojo(String schema, String tableName, String remark, List<MetaDataColumnPojo> fieldList) {
        this.schema = schema;
        this.tableName = tableName;
        this.remark = remark;
        this.fieldList = fieldList;
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
    public List<MetaDataColumnPojo> getFieldList() {
        return fieldList;
    }

    @Override
    public void setFieldList(List<MetaDataColumnPojo> fieldList) {
        this.fieldList = fieldList;
    }

    @Override
    public String toString() {
        return "{" + tableName + ", " + fieldList + "}";
    }
}
